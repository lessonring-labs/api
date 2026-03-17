package com.lessonring.api.booking.api;

import com.lessonring.api.booking.api.request.BookingCreateRequest;
import com.lessonring.api.booking.api.response.BookingResponse;
import com.lessonring.api.booking.application.BookingLockFacade;
import com.lessonring.api.booking.application.BookingService;
import com.lessonring.api.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingLockFacade bookingLockFacade;
    private final BookingService bookingService;

    @PostMapping
    public ApiResponse<BookingResponse> create(@RequestBody @Valid BookingCreateRequest request) {
        return ApiResponse.success(new BookingResponse(bookingLockFacade.create(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> get(@PathVariable Long id) {
        return ApiResponse.success(new BookingResponse(bookingService.get(id)));
    }

    @GetMapping
    public ApiResponse<List<BookingResponse>> getAll() {
        return ApiResponse.success(
                bookingService.getAll()
                        .stream()
                        .map(BookingResponse::new)
                        .toList()
        );
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<BookingResponse> cancel(@PathVariable Long id) {
        return ApiResponse.success(new BookingResponse(bookingLockFacade.cancel(id)));
    }

    @PatchMapping("/{id}/no-show")
    public ApiResponse<BookingResponse> markNoShow(@PathVariable Long id) {
        return ApiResponse.success(new BookingResponse(bookingService.markNoShow(id)));
    }

    @PatchMapping("/no-show/process")
    public ApiResponse<Integer> processNoShowBatch() {
        return ApiResponse.success(bookingService.markNoShowTargets());
    }
}