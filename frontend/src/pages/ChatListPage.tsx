import { useEffect, useState } from "react"
import { useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import { getMyChats, getChatById, ChatResponse } from "../api/chatApi"
import "./ChatListPage.css"

interface ChatWithDetails extends ChatResponse {
  otherParticipantName?: string
}

export default function ChatListPage() {
  const { user, token } = useAuth()
  const navigate = useNavigate()
  const [chats, setChats] = useState<ChatWithDetails[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!token || !user) {
      navigate("/login")
      return
    }

    const fetchChats = async () => {
      try {
        setLoading(true)
        setError(null)
        const chatList = await getMyChats(token)

        const chatsWithDetails = await Promise.all(
          chatList.map(async (chat) => {
            if (chat.type === "PRIVATE") {
              try {
                const chatDetail = await getChatById(chat.id, token!)
                const otherParticipant = chatDetail.participants.find(
                  (p: any) => p.participantId !== user?.id
                )
                if (otherParticipant) {
                  const name = otherParticipant.surname
                    ? `${otherParticipant.name} ${otherParticipant.surname}`
                    : otherParticipant.name
                  return { ...chat, otherParticipantName: name }
                }
              } catch (err) {
                console.error("Error fetching chat details:", err)
              }
            }
            return chat
          })
        )
        
        setChats(chatsWithDetails)
      } catch (err: any) {
        console.error("Error fetching chats:", err)
        setError(err.message || "Ошибка при загрузке чатов")
      } finally {
        setLoading(false)
      }
    }

    fetchChats()
  }, [token, user, navigate])

  const handleChatClick = async (chat: ChatResponse) => {
    try {
      if (chat.type === "PRIVATE") {
        const chatDetail = await getChatById(chat.id, token!)
        const otherParticipant = chatDetail.participants.find(
          (p: any) => p.participantId !== user?.id
        )
        if (otherParticipant) {
          navigate(`/chats/${otherParticipant.participantId}?type=private`)
        } else {
          navigate(`/chats/${chat.id}`)
        }
      } else {
        navigate(`/chats/${chat.id}`)
      }
    } catch (err) {
      console.error("Error opening chat:", err)
      navigate(`/chats/${chat.id}`)
    }
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))

    if (days === 0) {
      return "Сегодня"
    } else if (days === 1) {
      return "Вчера"
    } else if (days < 7) {
      return `${days} дн. назад`
    } else {
      return date.toLocaleDateString("ru-RU", {
        day: "numeric",
        month: "short",
        year: date.getFullYear() !== now.getFullYear() ? "numeric" : undefined,
      })
    }
  }

  if (loading) {
    return (
      <div className="chat-list-container">
        <div className="chat-list-header">
          <h1>Мои чаты</h1>
        </div>
        <div className="loading">Загрузка чатов...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="chat-list-container">
        <div className="chat-list-header">
          <h1>Мои чаты</h1>
        </div>
        <div className="error">Ошибка: {error}</div>
      </div>
    )
  }

  return (
    <div className="chat-list-container">
      <div className="chat-list-header">
        <h1>Мои чаты</h1>
      </div>
      <div className="chat-list">
        {chats.length === 0 ? (
          <div className="empty-state">
            <p>У вас пока нет чатов</p>
            <p className="empty-state-hint">Начните общение с друзьями!</p>
          </div>
        ) : (
          chats.map((chat) => (
            <div
              key={chat.id}
              className="chat-item"
              onClick={() => handleChatClick(chat)}
            >
              <div className="chat-item-content">
                <div className="chat-item-info">
                  <h3 className="chat-item-title">
                    {chat.type === "PRIVATE"
                      ? chat.otherParticipantName || "Личный чат"
                      : chat.name || `Чат #${chat.id}`}
                  </h3>
                  <p className="chat-item-meta">
                    {chat.type === "GROUP" && "Групповой чат"}
                    {chat.type === "PRIVATE" && "Приватный чат"}
                    {chat.type === "EVENT" && "Чат события"}
                  </p>
                </div>
                <div className="chat-item-date">
                  {formatDate(chat.createdAt)}
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

