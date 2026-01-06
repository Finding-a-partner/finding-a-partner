// src/components/FeedCard.tsx
import React from "react";

type FeedCardProps = {
  id: number;
  title: string;
  description?: string;
  date: string;
  time: string;
  ownerType: string;
  ownerId: number;
  visibility: string;
};

const FeedCard: React.FC<FeedCardProps> = ({
  title,
  description,
  date,
  time,
  ownerType,
  ownerId,
  visibility,
}) => {
  return (
    <div style={{
      border: "1px solid #ccc",
      borderRadius: "8px",
      padding: "16px",
      marginBottom: "12px",
      boxShadow: "2px 2px 8px rgba(0, 0, 0, 0.05)"
    }}>
      <h3>{title}</h3>
      {description && <p>{description}</p>}
      <small style={{ color: "#666" }}>
        {date} {time} • Владелец: {ownerType} #{ownerId} • Видимость: {visibility}
      </small>
    </div>
  );
};

export default FeedCard;
