package server.messageHandling;

import common.Reservation;
import common.Reward;
import common.TaggedConnection;
import common.messages.*;
import server.ClientHandler;
import server.ServerFacade;

import java.util.Set;

public class RewardsWithinDistanceRequestHandler implements IMessageHandler {
    static {
        ClientHandler.registerHandler(RewardsWithinDistanceRequest.class, new RewardsWithinDistanceRequestHandler());
    }

    @Override
    public Message processMessage(ServerFacade facade, TaggedConnection.Frame frame, ClientHandler.State state) {
        Message message = frame.getMessage();
        if(!(message instanceof RewardsWithinDistanceRequest)) //TODO:: Change exception
            throw new RuntimeException("Cannot process messages other than reward listing requests");

        if(state.currentUser == null) {
            return new NotAuthenticatedResponse();
        }

        RewardsWithinDistanceRequest request = (RewardsWithinDistanceRequest)message;
        Set<Reward> ans = facade.getRewardsInDistance(request.getLocation());

        return new RewardsWithinDistanceResponse(ans);
    }
}
