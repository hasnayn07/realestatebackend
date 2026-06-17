package com.realestatebackend.booking.service.impl;

import com.realestatebackend.auth.entity.User;
import com.realestatebackend.auth.repository.UserRepository;
import com.realestatebackend.booking.dto.PaymentResponse;
import com.realestatebackend.booking.dto.RecordPaymentRequest;
import com.realestatebackend.booking.entity.*;
import com.realestatebackend.booking.repository.BookingRepository;
import com.realestatebackend.booking.repository.InstallmentRepository;
import com.realestatebackend.booking.repository.PaymentRepository;
import com.realestatebackend.booking.service.PaymentService;
import com.realestatebackend.common.exception.BadRequestException;
import com.realestatebackend.common.exception.NotFoundException;
import com.realestatebackend.inventory.entity.UnitStatus;
import com.realestatebackend.inventory.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final InstallmentRepository installmentRepo;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final UnitService unitService;

    // where uploaded proof images go; configurable, defaults to ./uploads/proofs
    @Value("${app.upload.dir:uploads/proofs}")
    private String uploadDir;

    @Override
    @Transactional
    public PaymentResponse record(RecordPaymentRequest req, MultipartFile proof) {
        Installment installment = installmentRepo.findById(req.installmentId())
                .orElseThrow(() -> new NotFoundException("Installment not found: " + req.installmentId()));

        String storedPath = storeProof(proof);   // save the file, get back its path

        Payment payment = Payment.builder()
                .installment(installment)
                .amount(req.amount())
                .paidDate(req.paidDate())
                .proofImageUrl(storedPath)
                .status(PaymentStatus.PENDING)    // claimed, awaiting verification
                .build();

        return toResponse(paymentRepo.save(payment));
    }

    @Override
    @Transactional
    public PaymentResponse verify(UUID paymentId, UUID verifierUserId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("Only PENDING payments can be verified (current: "
                    + payment.getStatus() + ")");
        }

        User verifier = userRepo.findById(verifierUserId)
                .orElseThrow(() -> new NotFoundException("Verifier not found: " + verifierUserId));

        // 1) mark the payment verified + audit
        payment.setStatus(PaymentStatus.VERIFIED);
        payment.setVerifiedBy(verifier);
        payment.setVerifiedAt(Instant.now());

        // 2) credit the installment
        Installment inst = payment.getInstallment();
        inst.setAmountPaid(inst.getAmountPaid().add(payment.getAmount()));

        // 3) recompute the installment status
        if (inst.getAmountPaid().compareTo(inst.getAmountDue()) >= 0) {
            inst.setStatus(InstallmentStatus.PAID);
        } else {
            inst.setStatus(InstallmentStatus.PARTIAL);
        }

        // 4) if every installment of the booking is PAID -> complete the booking
        Booking booking = inst.getBooking();
        List<Installment> all =
                installmentRepo.findByBooking_IdOrderByInstallmentNumberAsc(booking.getId());
        boolean allPaid = all.stream().allMatch(i -> i.getStatus() == InstallmentStatus.PAID);
        if (allPaid) {
            booking.setStatus(BookingStatus.COMPLETED);
            unitService.changeStatus(booking.getUnit().getId(), UnitStatus.POSSESSION);
        }

        return toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse reject(UUID paymentId, UUID verifierUserId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("Only PENDING payments can be rejected");
        }
        User verifier = userRepo.findById(verifierUserId)
                .orElseThrow(() -> new NotFoundException("Verifier not found: " + verifierUserId));
        payment.setStatus(PaymentStatus.REJECTED);
        payment.setVerifiedBy(verifier);
        payment.setVerifiedAt(Instant.now());
        return toResponse(payment);   // balances untouched
    }

    // --- file storage ---

    private String storeProof(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Proof image is required");
        }
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException e) {
            throw new BadRequestException("Failed to store proof image: " + e.getMessage());
        }
    }

    private PaymentResponse toResponse(Payment p) {
        User v = p.getVerifiedBy();
        return new PaymentResponse(
                p.getId(), p.getInstallment().getId(), p.getAmount(), p.getPaidDate(),
                p.getProofImageUrl(), p.getStatus(),
                v != null ? v.getId() : null,
                v != null ? v.getFullName() : null,
                p.getVerifiedAt(), p.getCreatedAt()
        );
    }
}