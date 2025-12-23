package com.gzhu;

import java.util.ArrayList;

// 自定义链表存储航班信息
class FlightList {
    FlightNode head;

    public FlightList() {
        head = null;
    }

    public void add(FlightNode node) {
        if (head == null) {
            head = node;
        } else {
            FlightNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = node;
        }
    }

    // 查询方法
    public ArrayList<FlightNode> searchByFlightNumber(String flightNumber) {
        ArrayList<FlightNode> result = new ArrayList<>();
        FlightNode current = head;
        while (current != null) {
            if (current.flightNumber.equalsIgnoreCase(flightNumber)) {
                result.add(current);
            }
            current = current.next;
        }
        return result;
    }

    public ArrayList<FlightNode> searchByDeparture(String departure) {
        ArrayList<FlightNode> result = new ArrayList<>();
        FlightNode current = head;
        while (current != null) {
            if (current.departure.equals(departure)) {
                result.add(current);
            }
            current = current.next;
        }
        return result;
    }

    public ArrayList<FlightNode> searchByDestination(String destination) {
        ArrayList<FlightNode> result = new ArrayList<>();
        FlightNode current = head;
        while (current != null) {
            if (current.destination.equals(destination)) {
                result.add(current);
            }
            current = current.next;
        }
        return result;
    }

    public ArrayList<FlightNode> searchByDays(String searchDays) {
        ArrayList<FlightNode> result = new ArrayList<>();
        FlightNode current = head;

        // 解析查询的多个星期
        HashSet queryDays = parseSearchDays(searchDays);

        while (current != null) {
            // 检查航班是否包含所有查询的星期
            boolean containsAll = true;
            String[] queryDaysArray = queryDays.toSortedArray();
            for (String day : queryDaysArray) {
                if (!current.days.contains(day)) {
                    containsAll = false;
                    break;
                }
            }
            if (containsAll && queryDays.size() > 0) {
                result.add(current);
            }
            current = current.next;
        }
        return result;
    }

    private HashSet parseSearchDays(String daysStr) {
        HashSet daySet = new HashSet();
        String[] parts = daysStr.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.matches("[1-7]")) {
                daySet.add(trimmed);
            } else {
                // 尝试处理范围，如"1-3"
                if (trimmed.matches("[1-7]-[1-7]")) {
                    String[] range = trimmed.split("-");
                    int start = Integer.parseInt(range[0]);
                    int end = Integer.parseInt(range[1]);
                    for (int i = start; i <= end; i++) {
                        daySet.add(String.valueOf(i));
                    }
                }
            }
        }
        return daySet;
    }

    public ArrayList<FlightNode> searchByDepartureTime(String time) {
        ArrayList<FlightNode> result = new ArrayList<>();
        FlightNode current = head;
        // 移除查询时间中的冒号
        String timeWithoutColon = time.replace(":", "");

        while (current != null) {
            if (current.departureTime.compareTo(timeWithoutColon) >= 0) {
                result.add(current);
            }
            current = current.next;
        }
        return result;
    }

    public ArrayList<FlightNode> searchByArrivalTime(String time) {
        ArrayList<FlightNode> result = new ArrayList<>();
        FlightNode current = head;
        // 移除查询时间中的冒号
        String timeWithoutColon = time.replace(":", "");

        while (current != null) {
            if (current.arrivalTime.compareTo(timeWithoutColon) <= 0) {
                result.add(current);
            }
            current = current.next;
        }
        return result;
    }

    public ArrayList<FlightNode> getAllFlights() {
        ArrayList<FlightNode> result = new ArrayList<>();
        FlightNode current = head;
        while (current != null) {
            result.add(current);
            current = current.next;
        }
        return result;
    }

}