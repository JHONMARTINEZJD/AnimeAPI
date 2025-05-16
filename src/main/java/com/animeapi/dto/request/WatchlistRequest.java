package com.animeapi.dto.request;

import com.animeapi.model.WatchStatus;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class WatchlistRequest {
    @NotNull
    private WatchStatus status;

    public WatchStatus getStatus() {
        return status;
    }

    public void setStatus(WatchStatus status) {
        this.status = status;
    }
}
