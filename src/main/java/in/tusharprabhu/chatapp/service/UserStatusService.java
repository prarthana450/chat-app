package in.tusharprabhu.chatapp.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class UserStatusService {

    private final Set<String> onlineUsers =
            ConcurrentHashMap.newKeySet();

    public void userConnected(String username) {
        onlineUsers.add(username);
    }

    public void userDisconnected(String username) {
        onlineUsers.remove(username);
    }

    public boolean isUserOnline(String username) {
        return onlineUsers.contains(username);
    }

    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }
}