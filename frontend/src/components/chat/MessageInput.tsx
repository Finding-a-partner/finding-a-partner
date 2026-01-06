import { useState } from "react"
import { chatSocket } from "../../api/chatSocket"

type Props = {
  chatId: number
  senderId: number
}

export default function MessageInput({ chatId, senderId }: Props) {
  
  console.log("MessageInput rendered")
  const [text, setText] = useState("")

  const send = () => {
    if (!text.trim()) return
    chatSocket.send(chatId, senderId, text)
    setText("")
  }

  return (
    <div style={{ display: "flex", marginTop: 10 }}>
      <input
        value={text}
        onChange={e => setText(e.target.value)}
        style={{ flex: 1 }}
        onKeyDown={e => e.key === "Enter" && send()}
      />
      <button onClick={send}>Send</button>
    </div>
  )
}
