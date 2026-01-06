import { Message } from "../../types"

type Props = {
  message: Message
  isMine: boolean
}

export default function MessageItem({ message, isMine }: Props) {
  return (
    <div
      style={{
        textAlign: isMine ? "right" : "left",
        padding: "6px 10px",
      }}
    >
      <span
        style={{
          display: "inline-block",
          background: isMine ? "#DCF8C6" : "#FFF",
          padding: 8,
          borderRadius: 6,
        }}
      >
        {message.content}
      </span>
    </div>
  )
}
