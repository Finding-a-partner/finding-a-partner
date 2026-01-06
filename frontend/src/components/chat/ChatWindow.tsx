import { Message } from "../../types"
import MessageList from "./MessageList"
import MessageInput from "./MessageInput"

type Props = {
  chatId: number
  messages: Message[]
  senderId: number
}

export default function ChatWindow({ chatId, messages, senderId }: Props) {
  return (
    <div style={{ maxWidth: 600, margin: "0 auto" }}>
      <MessageList messages={messages} senderId={senderId} />
      <MessageInput chatId={chatId} senderId={senderId} />
    </div>
  )
}
