import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class SmartOffice {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("SmartOffice");
        logger.setLevel(Level.INFO);
        Office office = Office.getInstance(logger);
        office.configureRooms(2);
        System.out.println(office.setRoomCapacity(1, 5));
        System.out.println(office.blockRoom(1, LocalTime.now(), 30));
        System.out.println(office.addOccupant(1, 2));
        System.out.println(office.roomStatus(1));
    }
}

class Office {
    private static Office instance;
    private final Map<Integer, MeetingRoom> rooms = new ConcurrentHashMap<>();
    private final Logger logger;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Office(Logger logger) {
        this.logger = logger;
        scheduler.scheduleAtFixedRate(this::checkAutoRelease, 30, 30, TimeUnit.SECONDS);
    }
    public static synchronized Office getInstance(Logger logger) {
        if (instance == null) instance = new Office(logger);
        return instance;
    }
    public synchronized void configureRooms(int count) {
        rooms.clear();
        for (int i = 1; i <= count; i++) {
            rooms.put(i, new MeetingRoom(i, logger));
        }
    }
    public synchronized String setRoomCapacity(int roomId, int capacity) {
        MeetingRoom r = rooms.get(roomId);
        if (r == null) return "Room " + roomId + " does not exist.";
        r.setMaxCapacity(capacity);
        return "Room " + roomId + " maximum capacity set to " + capacity + ".";
    }
    public synchronized String blockRoom(int roomId, LocalTime startTime, int durationMin) {
        MeetingRoom r = rooms.get(roomId);
        if (r == null) return "Room " + roomId + " does not exist.";
        return r.blockBooking(startTime, durationMin);
    }
    public synchronized String cancelRoom(int roomId) {
        MeetingRoom r = rooms.get(roomId);
        if (r == null) return "Room " + roomId + " does not exist.";
        return r.cancelBooking();
    }
    public synchronized String addOccupant(int roomId, int count) {
        MeetingRoom r = rooms.get(roomId);
        if (r == null) return "Room " + roomId + " does not exist.";
        return r.setOccupants(count);
    }
    public synchronized String roomStatus(int roomId) {
        MeetingRoom r = rooms.get(roomId);
        if (r == null) return "Room " + roomId + " does not exist.";
        return r.status();
    }
    private void checkAutoRelease() {
        Instant now = Instant.now();
        for (MeetingRoom r : rooms.values()) {
            r.maybeAutoRelease(now);
        }
    }
}

class MeetingRoom {
    private final int id;
    private int maxCapacity = 10;
    private int currentOccupants = 0;
    private Booking currentBooking = null;
    private final Logger logger;
    private final List<RoomObserver> observers = new ArrayList<>();
    private Instant bookingTimestamp;
    public MeetingRoom(int id, Logger logger) {
        this.id = id;
        this.logger = logger;
        observers.add(new Light(id, logger));
        observers.add(new AirConditioner(id, logger));
    }
    public synchronized void setMaxCapacity(int capacity) {
        this.maxCapacity = capacity;
    }
    public synchronized String blockBooking(LocalTime startTime, int durationMin) {
        if (currentBooking != null && currentBooking.conflictsWith(startTime, durationMin)) {
            return "Room " + id + " is already booked.";
        }
        currentBooking = new Booking(startTime, durationMin);
        bookingTimestamp = Instant.now();
        return "Room " + id + " booked.";
    }
    public synchronized String cancelBooking() {
        if (currentBooking == null) return "Room " + id + " is not booked.";
        currentBooking = null;
        bookingTimestamp = null;
        return "Booking cancelled for room " + id;
    }
    public synchronized String setOccupants(int count) {
        if (count > maxCapacity) return "Capacity exceeded";
        this.currentOccupants = count;
        onOccupancyChange();
        return "Room " + id + " occupants set to " + count;
    }
    public synchronized String status() {
        return "Room " + id + " | Capacity: " + maxCapacity + " | Occupants: " + currentOccupants + " | Booked: " + (currentBooking == null ? "No" : currentBooking.summary());
    }
    private void onOccupancyChange() {
        boolean occupied = currentOccupants >= 2;
        for (RoomObserver o : observers) o.update(occupied);
    }
    public synchronized void maybeAutoRelease(Instant now) {
        if (currentBooking == null || bookingTimestamp == null) return;
        if (currentOccupants == 0 && Duration.between(bookingTimestamp, now).toMinutes() >= 5) {
            currentBooking = null;
            bookingTimestamp = null;
            logger.info("Room " + id + " booking auto-released.");
        }
    }
}

class Booking {
    private final LocalTime start;
    private final int durationMinutes;
    public Booking(LocalTime start, int durationMinutes) {
        this.start = start;
        this.durationMinutes = durationMinutes;
    }
    public boolean conflictsWith(LocalTime newStart, int newDuration) {
        LocalTime end = start.plusMinutes(durationMinutes);
        LocalTime newEnd = newStart.plusMinutes(newDuration);
        return !(newEnd.isBefore(start) || newStart.isAfter(end));
    }
    public String summary() {
        return start.toString() + " for " + durationMinutes + "min";
    }
}

interface RoomObserver {
    void update(boolean occupied);
}

class Light implements RoomObserver {
    private final int roomId;
    private final Logger logger;
    private boolean on = false;
    public Light(int roomId, Logger logger) { this.roomId = roomId; this.logger = logger; }
    public void update(boolean occupied) {
        boolean newState = occupied;
        if (newState != on) {
            on = newState;
            logger.info("Light in room " + roomId + (on ? " ON" : " OFF"));
        }
    }
}

class AirConditioner implements RoomObserver {
    private final int roomId;
    private final Logger logger;
    private boolean on = false;
    public AirConditioner(int roomId, Logger logger) { this.roomId = roomId; this.logger = logger; }
    public void update(boolean occupied) {
        boolean newState = occupied;
        if (newState != on) {
            on = newState;
            logger.info("AC in room " + roomId + (on ? " ON" : " OFF"));
        }
    }
}
