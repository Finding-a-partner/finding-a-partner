import { Message } from "../../types"
import MessageItem from "./MessageItem"

type Props = {
  messages: Message[]
  senderId: number
}

export default function MessageList({ messages, senderId }: Props) {
  console.log("[MessageList] Rendering with messages:", messages.length, messages)
  
  if (messages.length === 0) {
    return (
      <div style={{ height: 400, overflowY: "auto", border: "1px solid #ccc", padding: 20, textAlign: "center" }}>
        <p>Нет сообщений. Начните общение!</p>
      </div>
    )
  }
  
  return (
    <div style={{ height: 400, overflowY: "auto", border: "1px solid #ccc" }}>
      {messages.map(m => (
        <MessageItem
          key={m.id}
          message={m}
          isMine={m.senderId === senderId}
        />
      ))}
    </div>
  )
}
