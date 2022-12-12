package server;

import common.Reservation;

import java.util.Map;
import java.util.TreeMap;

public class ReservationCollection extends LockableCollection {
    private Map<Integer, Reservation> reservations;

    public ReservationCollection() {
        super();
        reservations = new TreeMap<>();
    }

    public void addReservation(Reservation r) {
        reservations.put(r.getId(), r);
    }

    public Reservation getReservation(int id) {
        return reservations.get(id);
    }

    public int getNumberReservations() {
        return reservations.size();
    }
}
