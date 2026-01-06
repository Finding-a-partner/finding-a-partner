import { useEffect, useState, useRef } from "react"
import { useParams, useSearchParams } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import { chatSocket } from "../api/chatSocket"
import { getOrCreatePrivateChat, getChatMessages } from "../api/chatApi"
import { Message } from "../types"
import ChatWindow from "../components/chat/ChatWindow"

export default function ChatPage() {
  
  console.log("ChatPage rendered")
  const { id } = useParams()
  const [searchParams] = useSearchParams()
  const isPrivateChat = searchParams.get("type") === "private"
  const { user, token } = useAuth()
  const [messages, setMessages] = useState<Message[]>([])
  const [chatId, setChatId] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const previousChatIdRef = useRef<number | null>(null)
  const isConnectedRef = useRef(false)

  useEffect(() => {
    if (!token || !user || !id) return

    const initializeChat = async () => {
      try {
        setLoading(true)
        let finalChatId: number

        if (isPrivateChat) {
          const otherUserId = Number(id)
          console.log("Creating/getting private chat with user:", otherUserId)
          const chat = await getOrCreatePrivateChat(otherUserId, token)
          finalChatId = chat.id
          console.log("Private chat ID:", finalChatId)
        } else {
          finalChatId = Number(id)
        }

        setChatId(finalChatId)

        try {
          console.log("[ChatPage] Loading messages for chat:", finalChatId)
          const existingMessages = await getChatMessages(finalChatId, token)
          console.log("[ChatPage] Successfully loaded messages:", existingMessages.length, existingMessages)
          if (existingMessages && existingMessages.length > 0) {
            setMessages(existingMessages)
          } else {
            console.log("[ChatPage] No messages found, setting empty array")
            setMessages([])
          }
        } catch (error: any) {
          console.error("[ChatPage] Error loading messages:", error)
          console.error("[ChatPage] Error details:", error.message, error.stack)
          setMessages([])
        }
      } catch (error) {
        console.error("Error initializing chat:", error)
      } finally {
        setLoading(false)
      }
    }

    initializeChat()
  }, [id, isPrivateChat, token, user])

  useEffect(() => {
    if (!token || !user || !chatId || loading) return

    console.log("ChatPage useEffect", { chatId, token: !!token, user: !!user })

    if (isConnectedRef.current && previousChatIdRef.current === chatId) {
      return
    }

    if (previousChatIdRef.current !== null && previousChatIdRef.current !== chatId) {
      chatSocket.unsubscribe(previousChatIdRef.current)
    }

    if (!isConnectedRef.current) {
      chatSocket.connect(token, () => {
        isConnectedRef.current = true
        chatSocket.subscribe(chatId, message => {
          console.log("New message received:", message)
          setMessages(prev => [...prev, message])
        })
        previousChatIdRef.current = chatId
      })
    } else {
      chatSocket.subscribe(chatId, message => {
        console.log("New message received:", message)
        setMessages(prev => [...prev, message])
      })
      previousChatIdRef.current = chatId
    }

    return () => {
      if (previousChatIdRef.current !== null) {
        chatSocket.unsubscribe(previousChatIdRef.current)
      }
      chatSocket.disconnect()
      isConnectedRef.current = false
      previousChatIdRef.current = null
    }
  }, [chatId, token, user, loading])

  if (loading || !chatId) {
    return <div>Загрузка чата...</div>
  }

  console.log("[ChatPage] Rendering ChatWindow with:", { chatId, messagesCount: messages.length, senderId: user!.id })

  return (
    <ChatWindow
      chatId={chatId}
      messages={messages}
      senderId={user!.id}
    />
  )
}
