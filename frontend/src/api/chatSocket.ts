import SockJS from "sockjs-client"
import { Client, IMessage } from "@stomp/stompjs"
import { Message } from "../types"

const WS_URL = process.env.REACT_APP_WS_URL || "http://localhost:8085/ws"

class ChatSocket {
  private client: Client | null = null
  private connected = false
  private subscriptions: Map<number, any> = new Map()

  connect(token: string, onConnect?: () => void) {
    if (this.client) {
      this.disconnect()
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      onConnect: () => {
        this.connected = true
        console.log("WS connected")
        onConnect?.()
      },
      onDisconnect: () => {
        this.connected = false
        this.subscriptions.clear()
      },
      onStompError: err => {
        console.error("STOMP error", err)
        this.connected = false
      },
      debug: () => {},
    })

    this.client.activate()
  }

  send(chatId: number, senderId: number, content: string) {
    console.log("SEND WS MESSAGE", { chatId, senderId, content })
    if (!this.connected || !this.client) {
      console.warn("WS not connected, message skipped")
      return
    }

    try {
      this.client.publish({
        destination: "/app/chat.sendMessage",
        body: JSON.stringify({ chatId, senderId, content }),
      })
    } catch (error) {
      console.error("Error sending message:", error)
    }
  }

  subscribe(chatId: number, callback: (msg: Message) => void) {
    if (!this.connected || !this.client) {
      console.warn("WS not connected, cannot subscribe yet")
      return
    }

    this.unsubscribe(chatId)

    try {
      const subscription = this.client.subscribe(`/topic/chat.${chatId}`, (message: IMessage) => {
        try {
          const parsed = JSON.parse(message.body)
          console.log("Received message:", parsed)
          callback(parsed)
        } catch (error) {
          console.error("Error parsing message:", error, message.body)
        }
      })
      this.subscriptions.set(chatId, subscription)
      console.log(`Subscribed to chat ${chatId}`)
    } catch (error) {
      console.error("Error subscribing:", error)
    }
  }

  unsubscribe(chatId: number) {
    const subscription = this.subscriptions.get(chatId)
    if (subscription) {
      subscription.unsubscribe()
      this.subscriptions.delete(chatId)
      console.log(`Unsubscribed from chat ${chatId}`)
    }
  }

  disconnect() {
    this.connected = false
    this.subscriptions.forEach((sub, chatId) => {
      try {
        sub.unsubscribe()
      } catch (error) {
        console.error(`Error unsubscribing from chat ${chatId}:`, error)
      }
    })
    this.subscriptions.clear()
    this.client?.deactivate()
    this.client = null
  }
}

export const chatSocket = new ChatSocket()
