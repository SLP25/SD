package common;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * A reservation of a scooter
 */
public class Reservation extends Lockable {
    /**
     * The price of having the scooter reserved for 1 second
     */
    private static int pricePerSecond = 20;

    /**
     * The price of travelling 1 unit with the scooter
     */
    private static int pricePerUnitTravelled = 10;

    /**
     * The unique identifier of the reserved scooter
     */
    private int scooterId;

    /**
     * The username of the user who made the reservation
     */
    private String user;

    /**
     * The location of the scooter when the reservation was made
     */
    private Location startLocation;

    /**
     * The location of the scooter when the reservation was terminated
     */
    private Location endLocation;

    /**
     * The date/time the reservation was made
     */
    private LocalDateTime startTime;

    /**
     * The date/time the reservation was terminated
     */
    private LocalDateTime endTime;

    /**
     * Parameterized constructor
     * @param id the id of the reservation
     * @param user the username of the user who made the reservation
     * @param startLocation the location of the scooter when the reservation was made
     * @param endLocation the location of the scooter when the reservation was terminated
     * @param startTime the date/time the reservation was made
     * @param endTime the date/time the reservation was terminated
     */
    public Reservation(int id, String user, Location startLocation, Location endLocation,
                       LocalDateTime startTime, LocalDateTime endTime) {
        super(id);
        this.user = user;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Parameterized constructor for a reservation yet to be terminated
     * @param id the id of the reservation
     * @param user the username of the user who made the reservation
     * @param startLocation the location of the scooter when the reservation was made
     * @param startTime the date/time the reservation was made
     */
    public Reservation(int id, String user, Location startLocation, LocalDateTime startTime) {
        this(id, user, startLocation, null, startTime, null);
    }

    /**
     * Copy constructor
     * @param r the reservation to copy
     */
    public Reservation(Reservation r) {
        this(r.getId(), r.getUser(), r.startLocation, r.endLocation, r.startTime, r.endTime);
    }

    /**
     * Default constructor
     */
    public Reservation() {
        this(-1, "", null, null);
    }

    /**
     * Gets the price of having the scooter reserved for 1 second
     * @return the price of having the scooter reserved for 1 second
     */
    public static int getPricePerSecond() {
        return pricePerSecond;
    }

    /**
     * Gets the price of travelling 1 unit with the scooter
     * @return the price of travelling 1 unit with the scooter
     */
    public static int getPricePerUnitTravelled() {
        return pricePerUnitTravelled;
    }

    /**
     * Gets the username of the user who made the reservation
     *
     * @return the username of the user who made the reservation
     */
    public String getUser() {
        return user;
    }

    /**
     * Gets the location of the scooter when the reservation was made
     *
     * @return the location of the scooter when the reservation was made
     */
    public Location getStartLocation() {
        return startLocation;
    }

    /**
     * Gets the location of the scooter when the reservation was terminated
     * @return the location of the scooter when the reservation was terminated
     */
    public Location getEndLocation() {
        return endLocation;
    }

    /**
     * Gets the date/time the reservation was made
     * @return the date/time the reservation was made
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the date/time the reservation was terminated
     * @return the date/time the reservation was terminated
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the price of having the scooter reserved for 1 second
     * @param price the new price of having the scooter reserved for 1 second
     */
    public static void setPricePerSecond(int price) {
        pricePerSecond = price;
    }

    /**
     * Sets the price of travelling 1 unit with the scooter
     * @param price the new price of travelling 1 unit with the scooter
     */
    public static void setPricePerUnitTravelled(int price) {
        pricePerUnitTravelled = price;
    }

    /**
     * Terminates the reservation at the given location at the given timestamp
     * @param location the new location of the scooter
     * @param time the date/timn the reservation was terminated at
     */
    public void terminate(Location location, LocalDateTime time) {
        this.endLocation = location;
        this.endTime = time;
    }

    /**
     * Terminates the reservation at the given location
     * @param location the new location of the scooter
     */
    public void terminate(Location location) {
        terminate(location, LocalDateTime.now());
    }

    /**
     * Gets the price the user has to pay for the reservation
     *
     * The price depends on the time the scooter was reserved for and
     * the distance travelled during that time.
     *
     * More precisely, it is the sum of two products. One is the product of
     * the duration of the reservation (in seconds) with the {@link, #pricePerSecond}; and
     * the other the product of the distance travelled with the {@link, #pricePerUnitTravelled}.
     *
     * @return the price the user has to pay for the reservation
     */
    public int getCost() {
        int timeElapsed = (int)ChronoUnit.SECONDS.between(startTime, endTime);
        int distanceTravelled = Location.distance(startLocation, endLocation);

        return pricePerSecond * timeElapsed + pricePerUnitTravelled * distanceTravelled;
    }

    @Override
    public Object clone() {
        return new Reservation(this);
    }

    @Override
    public String toString() {
        return String.format("Id: %s, User: %s, Scooter ID: %d, Start Location: %s, Start Time: %s, End Location: %s," +
                " End Time: %s", this.getId(), user, scooterId, startLocation.toString(), startTime.toString(),
                endLocation.toString(), endTime.toString());
    }
}
