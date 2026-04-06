"use strict";

var usernamePage = document.querySelector("#username-page");
var chatPage = document.querySelector("#chat-page");
var usernameForm = document.querySelector("#usernameForm");
var messageForm = document.querySelector("#messageForm");
var messageInput = document.querySelector("#message");
var messageArea = document.querySelector("#messageArea");
var connectingElement = document.querySelector(".connecting");

var stompClient = null;
var username = null;
//mycode
var password = null;

var colors = [
  "#2196F3",
  "#32c787",
  "#00BCD4",
  "#ff5652",
  "#ffc107",
  "#ff85af",
  "#FF9800",
  "#39bbb0",
  "#fcba03",
  "#fc0303",
  "#de5454",
  "#b9de54",
  "#54ded7",
  "#54ded7",
  "#1358d6",
  "#d611c6",
];

function connect(event) {
  username = document.querySelector("#name").value.trim();
  password = document.querySelector("#password").value;
  if (username) {
    //Enter your password
    if (password == "hello") {
      usernamePage.classList.add("hidden");
      chatPage.classList.remove("hidden");

      var socket = new SockJS("/ws");
      stompClient = Stomp.over(socket);

      stompClient.connect({}, onConnected, onError);
    } else {
      let mes = document.getElementById("mes");
      mes.innerText = "Wrong password";
    }
  }
  event.preventDefault();
}

function onConnected() {
  // Enable leave warning ONLY after login
  window.onbeforeunload = function () {
    return "Are you sure you want to leave the chat?";
  };
  window.addEventListener("beforeunload", function () {
    if (stompClient) {
      stompClient.send(
        "/app/chat.register",
        {},
        JSON.stringify({
          sender: username,
          type: "LEAVE"
        })
      );
    }
  });

  stompClient.subscribe("/topic/public", onMessageReceived);

  stompClient.send(
    "/app/chat.register",
    {},
    JSON.stringify({
      sender: username,
      type: "JOIN"
    })
  );

  connectingElement.classList.add("hidden");
}
function logout() {
  if (stompClient && stompClient.connected) {

    // Send OFFLINE status
    stompClient.send(
      "/app/chat.register",
      {},
      JSON.stringify({
        sender: username,
        type: "LEAVE"
      })
    );

    // Disconnect WebSocket
    stompClient.disconnect(function () {
      console.log("Disconnected");
    });
  }

  // Reload page
  window.location.reload();
}
function onError(error) {
  connectingElement.textContent =
    "Could not connect to WebSocket! Please refresh the page and try again or contact your administrator.";
  connectingElement.style.color = "red";
}
function onMessageReceived(payload) {
  var message = JSON.parse(payload.body);

  if (message.type === "STATUS") {

    var messageElement = document.createElement("li");
    messageElement.classList.add("event-message");

    var statusText;

    if (message.status === "ONLINE") {
      statusText = "🟢 " + message.sender + " is ONLINE";
    } else {
      statusText = "🔴 " + message.sender + " is OFFLINE";
    }

    var textElement = document.createElement("p");
    textElement.appendChild(
      document.createTextNode(statusText)
    );

    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);

    messageArea.scrollTop = messageArea.scrollHeight;

    return;
  }

  // Normal chat message handling
// Normal chat message handling
var messageElement = document.createElement("li");
messageElement.classList.add("chat-message");

// Show own messages on right side
if (message.sender === username) {
  messageElement.classList.add("own-message");
}

// Avatar (first letter)
var avatarElement = document.createElement("i");
var avatarText = document.createTextNode(message.sender[0]);
avatarElement.appendChild(avatarText);
avatarElement.style.backgroundColor =
  getAvatarColor(message.sender);

// Sender name
var senderElement = document.createElement("span");
senderElement.appendChild(
  document.createTextNode(message.sender)
);

// Message text
var textElement = document.createElement("p");
textElement.appendChild(
  document.createTextNode(message.content)
);

// Append elements
messageElement.appendChild(avatarElement);
messageElement.appendChild(senderElement);
messageElement.appendChild(textElement);

messageArea.appendChild(messageElement);

messageArea.scrollTop = messageArea.scrollHeight;
}
function send(event) {
  var messageContent = messageInput.value.trim();

  if (messageContent && stompClient) {
    var chatMessage = {
      sender: username,
      content: messageInput.value,
      type: "CHAT",
    };

    stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
    messageInput.value = "";
  }
  event.preventDefault();
}

/**
 * Handles the received message and updates the chat interface accordingly.
 * param {Object} payload - The payload containing the message data.
 */

function getAvatarColor(messageSender) {
  var hash = 0;
  for (var i = 0; i < messageSender.length; i++) {
    hash = 31 * hash + messageSender.charCodeAt(i);
  }

  var index = Math.abs(hash % colors.length);
  return colors[index];
}
window.addEventListener("beforeunload", function (e) {
  if (stompClient && stompClient.connected) {
    e.preventDefault();
    e.returnValue = "";
  }
});

usernameForm.addEventListener("submit", connect, true);
messageForm.addEventListener("submit", send, true);
