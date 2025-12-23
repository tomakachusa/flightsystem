package com.gzhu;

// 自定义航班信息节点（链表节点）
class FlightNode {
    String flightNumber;
    String departure;
    String destination;
    HashSet days; // 使用自定义的HashSet
    String departureTime;
    String arrivalTime;
    String aircraftType;
    String price;
    FlightNode next;

    public FlightNode(String flightNumber, String departure, String destination,
                      String daysStr, String departureTime, String arrivalTime,
                      String aircraftType, String price) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
        this.days = parseDays(daysStr);
        // 存储时间时去掉冒号
        this.departureTime = departureTime.replace(":", "");
        this.arrivalTime = arrivalTime.replace(":", "");
        this.aircraftType = aircraftType;
        this.price = price;
        this.next = null;
    }

    private HashSet parseDays(String daysStr) {
        HashSet daySet = new HashSet();
        String[] dayArray = daysStr.split(",");
        for (String day : dayArray) {
            String trimmed = day.trim();
            if (trimmed.matches("[1-7]")) {
                daySet.add(trimmed);
            }
        }
        return daySet;
    }

    public String getDaysString() {
        return days.getSortedString();
    }

    // 获取带冒号的时间格式
    public String getDepartureTimeFormatted() {
        return formatTimeWithColon(departureTime);
    }

    public String getArrivalTimeFormatted() {
        return formatTimeWithColon(arrivalTime);
    }

    private String formatTimeWithColon(String time) {
        if (time.length() == 4) {
            return time.substring(0, 2) + ":" + time.substring(2);
        }
        return time;
    }
}