package com.realestatebackend.booking.service.impl;

import com.realestatebackend.auth.entity.User;
import com.realestatebackend.auth.repository.UserRepository;
import com.realestatebackend.booking.dto.BookingResponse;
import com.realestatebackend.booking.dto.CreateBookingRequest;
import com.realestatebackend.booking.dto.InstallmentResponse;
import com.realestatebackend.booking.entity.*;
import com.realestatebackend.booking.repository.BookingRepository;
import com.realestatebackend.booking.repository.CustomerRepository;
import com.realestatebackend.booking.repository.InstallmentRepository;
import com.realestatebackend.booking.service.BookingService;
import com.realestatebackend.common.exception.BadRequestException;
import com.realestatebackend.common.exception.NotFoundException;
import com.realestatebackend.inventory.entity.Unit;
import com.realestatebackend.inventory.entity.UnitStatus;
import com.realestatebackend.inventory.repository.UnitRepository;
import com.realestatebackend.inventory.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final CustomerRepository customerRepo;
    private final InstallmentRepository installmentRepo;
    private final UnitRepository unitRepo;
    private final UserRepository userRepo;
    private final UnitService unitService;   // reuse the Module 2 state machine

    @Override
    @Transactional
    public BookingResponse create(CreateBookingRequest req) {
        // --- Part A: validate + wire references ---
        var customer = customerRepo.findById(req.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + req.customerId()));

        Unit unit = unitRepo.findById(req.unitId())
                .orElseThrow(() -> new NotFoundException("Unit not found: " + req.unitId()));

        if (unit.getStatus() != UnitStatus.AVAILABLE) {
            throw new BadRequestException("Unit is not AVAILABLE (current: " + unit.getStatus() + ")");
        }
        if (req.downPayment().compareTo(req.salePrice()) > 0) {
            throw new BadRequestException("Down payment cannot exceed sale price");
        }

        User agent = null;
        if (req.agentId() != null) {
            agent = userRepo.findById(req.agentId())
                    .orElseThrow(() -> new NotFoundException("Agent not found: " + req.agentId()));
        }

        // --- create the booking ---
        Booking booking = Booking.builder()
                .customer(customer)
                .unit(unit)
                .agent(agent)
                .salePrice(req.salePrice())
                .downPayment(req.downPayment())
                .numberOfInstallments(req.numberOfInstallments())
                .frequency(req.frequency())
                .installmentStartDate(req.installmentStartDate())
                .status(BookingStatus.ACTIVE)
                .build();
        booking = bookingRepo.save(booking);

        // --- Part B: generate the schedule ---
        List<Installment> schedule = generateSchedule(booking);
        installmentRepo.saveAll(schedule);

        // --- flip the unit via the existing state machine ---
        // AVAILABLE -> BOOKED -> ON_INSTALLMENTS (the state machine requires both steps)
        unitService.changeStatus(unit.getId(), UnitStatus.BOOKED);
        unitService.changeStatus(unit.getId(), UnitStatus.ON_INSTALLMENTS);

        return toResponse(booking, schedule);
    }

    /**
     * Splits (salePrice - downPayment) across N installments.
     * Every installment gets an even base amount; the LAST installment
     * absorbs the rounding remainder so the schedule sums to the exact total.
     */
    private List<Installment> generateSchedule(Booking booking) {
        BigDecimal financed = booking.getSalePrice().subtract(booking.getDownPayment());
        int n = booking.getNumberOfInstallments();
        int monthsStep = booking.getFrequency().getMonths();

        BigDecimal base = financed.divide(BigDecimal.valueOf(n), 2, RoundingMode.DOWN);
        BigDecimal lastAmount = financed.subtract(base.multiply(BigDecimal.valueOf(n - 1L)));

        List<Installment> list = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            BigDecimal amount = (i == n) ? lastAmount : base;
            Installment inst = Installment.builder()
                    .booking(booking)
                    .installmentNumber(i)
                    .dueDate(booking.getInstallmentStartDate().plusMonths((long) (i - 1) * monthsStep))
                    .amountDue(amount)
                    .amountPaid(BigDecimal.ZERO)
                    .status(InstallmentStatus.PENDING)
                    .build();
            list.add(inst);
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getById(UUID id) {
        Booking booking = findOrThrow(id);
        List<Installment> schedule =
                installmentRepo.findByBooking_IdOrderByInstallmentNumberAsc(id);
        return toResponse(booking, schedule);
    }

    @Override
    @Transactional
    public void cancel(UUID id) {
        Booking booking = findOrThrow(id);
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed booking");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        // return the unit to the pool
        unitService.changeStatus(booking.getUnit().getId(), UnitStatus.CANCELLED);
    }

    // --- helpers ---

    private Booking findOrThrow(UUID id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + id));
    }

    private BookingResponse toResponse(Booking b, List<Installment> schedule) {
        List<InstallmentResponse> instDtos = schedule.stream()
                .map(this::toInstallmentResponse).toList();

        BigDecimal outstanding = schedule.stream()
                .map(i -> i.getAmountDue().subtract(i.getAmountPaid()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var unit = b.getUnit();
        var customer = b.getCustomer();
        var agent = b.getAgent();

        return new BookingResponse(
                b.getId(),
                customer.getId(), customer.getFullName(), customer.getCnic(),
                unit.getId(), unit.getUnitNumber(), unit.getBlock().getProject().getName(),
                agent != null ? agent.getId() : null,
                agent != null ? agent.getFullName() : null,
                b.getSalePrice(), b.getDownPayment(),
                b.getNumberOfInstallments(), b.getFrequency(),
                b.getInstallmentStartDate(), b.getBookingDate(), b.getStatus(),
                outstanding,
                instDtos
        );
    }

    private InstallmentResponse toInstallmentResponse(Installment i) {
        return new InstallmentResponse(
                i.getId(), i.getBooking().getId(), i.getInstallmentNumber(),
                i.getDueDate(), i.getAmountDue(), i.getAmountPaid(),
                i.getAmountDue().subtract(i.getAmountPaid()),
                i.getStatus()
        );
    }
}