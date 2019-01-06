package com.checkin.app.checkin.Waiter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
class NavTableModel {
    private String table;
    private Host host;

    @Setter
    @Getter
    @NoArgsConstructor
    static class Host{
        private String tableNumber;
        private String customerName;
    }
}
