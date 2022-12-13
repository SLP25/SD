package server;

import common.Reservation;

import java.util.Map;
import java.util.TreeMap;

/**
 * A collection of scooter reservations
 */
public class ReservationCollection extends LockableCollection {
    /**
     * All reservations indexed by the identifier
     */
    private Map<Integer, Reservation> reservations;

    /**
     * Default constructor
     */
    public ReservationCollection() {
        super();
        reservations = new TreeMap<>();
    }

    /**
     * Adds a new reservation to the collection
     * @param r the new reservation
     */
    public void addReservation(Reservation r) {
        reservations.put(r.getId(), r);
    }

    /**
     * Gets the reservation with the given id
     * @param id the identifier of the collection
     * @return the reservation with the given id
     */
    public Reservation getReservation(int id) {
        return reservations.get(id);
    }

    /**
     * Gets the total number of reservations in the collection
     * @return the total number of reservations in the collection
     */
    public int getNumberReservations() {
        return reservations.size();
    }
}
