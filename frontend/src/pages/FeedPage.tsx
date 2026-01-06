import { useEffect, useState } from "react";
import FeedCard from "../components/FeedCard";
import { joinEvent } from "../api/feedApi"

interface FeedResponse {
  id: number;
  createdAt: string;
  ownerId: number;
  ownerType: string;
  title: string;
  description?: string;
  visibility: string;
  time: string;
  date: string;
}

const FeedPage = () => {
  const [feed, setFeed] = useState<FeedResponse[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [joiningEventId, setJoiningEventId] = useState<number | null>(null);
  useEffect(() => {
    const fetchFeed = async () => {
      try {
        const token = localStorage.getItem("token");

        const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
        const response = await fetch(`${API_URL}/feed`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          const text = await response.text();
          throw new Error(text || "Ошибка загрузки ленты");
        }

        const data: FeedResponse[] = await response.json();
        setFeed(data);
      } catch (err: any) {
        setError(err.message || "Ошибка загрузки");
      }
    };

    fetchFeed();
  }, []);

  const handleJoinEvent = async (eventId: number) => {
    setJoiningEventId(eventId);
    setLoading(true);
    try {
      await joinEvent(eventId);
      setFeed(feed.map(item => 
        item.id === eventId ? { ...item, isParticipating: true } : item
      ));
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
      setJoiningEventId(null);
    }
  };

  return (
    <div>
      <h2>Новостная лента</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {feed.length === 0 && !error ? (
        <p>Нет мероприятий</p>
      ) : (
        feed.map((item) => (
          <div key={item.id} style={{ marginBottom: "20px", border: "1px solid #eee", padding: "15px", borderRadius: "8px" }}>
          <FeedCard
            key={item.id}
            id={item.id}
            title={item.title}
            description={item.description}
            date={item.date}
            time={item.time}
            ownerType={item.ownerType}
            ownerId={item.ownerId}
            visibility={item.visibility}
          />
           <button
              onClick={() => handleJoinEvent(item.id)}
              disabled={loading && joiningEventId === item.id}
              style={{
                marginTop: "10px",
                padding: "8px 16px",
                backgroundColor: "#4caf50",
                color: "white",
                border: "none",
                borderRadius: "4px",
                cursor: "pointer"
              }}
            >
            Учавствовать
            </button>
            </div>
        ))
      )}
    </div>
  );
};

export default FeedPage;
