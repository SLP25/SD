package client;

import common.ClassLoader;
import common.Location;
import common.messages.*;
import common.*;
import utils.Pair;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Main client entry point
 */
public class Main {
    /**
     * Main client entry point
     *
     * @params args Ignored
     */
    public static void main(String[] args) {
        //magia negra do felicio

        ClassLoader.loadClasses(Message.class.getPackage().getName(),
                Arrays.asList(new String[]{"Message", "Exception"}));
        try (
                ClientFacade facade = new ClientFacade();
        ) {
            System.out.println(facade.authenticate("vasques", "password1234"));
            System.out.println(facade.register("batata", "password1234"));

            Map<Location, Integer> scooters = facade.getFreeScootersInDistance(new Location(10,10));
            System.out.println(scooters.size());
            for(Location l : scooters.keySet()) {
                System.out.println(l.toString());
            }

            Pair<Integer, Location> p = facade.reserveScooter(new Location(10, 10));
            System.out.println(p.getFirst());
            System.out.println(p.getSecond());

            Thread.sleep(5000);
            System.out.println(facade.endReservation(p.getFirst(), new Location(0,0)));
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }
}
