import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";

type FriendshipStatus = "PENDING" | "ACCEPTED" | "REJECTED" | "BLOCKED";

interface UserResponse {
  id: number;
  createdAt: string;
  login: string;
  email: string;
  description?: string;
  name: string;
  surname?: string;
}

interface FriendResponse {
  id: number;
  status: FriendshipStatus;
  friend: UserResponse;
}

const TABS: { label: string; status: FriendshipStatus | "INCOMING" }[] = [
  { label: "Друзья", status: "ACCEPTED" },
  { label: "Входящие заявки", status: "INCOMING" },
  { label: "Исходящие заявки", status: "PENDING" },
  { label: "Отклонённые", status: "REJECTED" },
  { label: "Заблокированные", status: "BLOCKED" },
];

const FriendsPage = () => {
  const [friends, setFriends] = useState<FriendResponse[]>([]);
  const [activeTab, setActiveTab] = useState<FriendshipStatus | "INCOMING">("ACCEPTED");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const userJson = localStorage.getItem("user");
  const user = userJson ? JSON.parse(userJson) : null;

  const fetchFriends = async (status: FriendshipStatus | "INCOMING") => {
    if (!user?.id) {
      navigate("/login");
      return;
    }

    try {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login");
        return;
      }

      setLoading(true);
      setError("");
      
      const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
      let response: Response;
      if (status === "INCOMING") {
        response = await fetch(
          `${API_URL}/users/friends/${user.id}/incoming`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );
      } else {
        response = await fetch(
          `${API_URL}/users/friends/${user.id}?status=${status}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );
      }

      if (!response.ok) {
        throw new Error(response.status === 401 
          ? "Требуется авторизация" 
          : "Ошибка при загрузке друзей");
      }

      const data: FriendResponse[] = await response.json();

      console.log(data)

      setFriends(data);
    } catch (err: any) {
      setError(err.message || "Произошла ошибка");
      if (err.message.includes("401")) {
        navigate("/login");
      }
    } finally {
      setLoading(false);
    }
  };

  const respondToRequest = async (requestId: number, accept: boolean) => {
    if (!user?.id) {
      navigate("/login");
      return;
    }

    try {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login");
        return;
      }

      const friendship = friends.find(f => f.id === requestId);
      if (!friendship) {
        throw new Error("Заявка не найдена");
      }

      const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
      const response = await fetch(
        `${API_URL}/users/friends/${requestId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            userId: friendship.friend.id,
            friendId: user.id,
            status: accept ? "ACCEPTED" : "REJECTED",
          }),
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Ошибка при обработке заявки");
      }

      await fetchFriends(activeTab);
    } catch (err: any) {
      setError(err.message || "Произошла ошибка");
    }
  };

  useEffect(() => {
    fetchFriends(activeTab);
  }, [activeTab]);

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("ru-RU");
  };

  return (
    <div style={{ maxWidth: "800px", margin: "0 auto", padding: "20px" }}>
      <h2>Мои друзья</h2>

      <div style={{ 
        display: "flex", 
        gap: "12px", 
        marginBottom: "24px",
        overflowX: "auto",
        paddingBottom: "8px"
      }}>
        {TABS.map((tab) => (
          <button
            key={tab.status}
            onClick={() => setActiveTab(tab.status)}
            style={{
              fontWeight: tab.status === activeTab ? "bold" : "normal",
              backgroundColor: tab.status === activeTab ? "#e3f2fd" : "#f5f5f5",
              padding: "8px 16px",
              border: "none",
              borderRadius: "20px",
              cursor: "pointer",
              whiteSpace: "nowrap",
              transition: "all 0.2s",
            }}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {loading && <p style={{ textAlign: "center" }}>Загрузка...</p>}
      {error && (
        <p style={{ color: "red", textAlign: "center", margin: "20px 0" }}>
          {error}
        </p>
      )}

      <div style={{ 
        display: "grid", 
        gap: "16px",
        gridTemplateColumns: "repeat(auto-fill, minmax(280px, 1fr))"
      }}>
        {friends.length === 0 && !loading ? (
          <p style={{ gridColumn: "1 / -1", textAlign: "center" }}>
            Нет пользователей в этой категории
          </p>
        ) : (
          friends.map((friendship) => (
            <div 
              key={friendship.id}
              style={{
                padding: "16px",
                borderRadius: "8px",
                backgroundColor: "#fff",
                boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
              }}
            >
              <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                <div style={{
                  width: "48px",
                  height: "48px",
                  borderRadius: "50%",
                  backgroundColor: "#e3f2fd",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontWeight: "bold",
                  fontSize: "18px"
                }}>
                  {friendship.friend.name.charAt(0)}
                  {friendship.friend.surname?.charAt(0)}
                </div>
                <div>
                  <h3 style={{ margin: 0 }}>
                    {friendship.friend.name} {friendship.friend.surname}
                  </h3>
                  <small style={{ color: "#666" }}>@{friendship.friend.login}</small>
                </div>
              </div>
              
              <div style={{ marginTop: "12px" }}>
                <p style={{ margin: "4px 0", color: "#666" }}>
                  <strong>Email:</strong> {friendship.friend.email}
                </p>
                {friendship.friend.description && (
                  <p style={{ margin: "8px 0" }}>
                    {friendship.friend.description}
                  </p>
                )}
                {activeTab === "ACCEPTED" && (
                  <p style={{ fontSize: "0.8rem", color: "#999", marginTop: "8px" }}>
                    В друзьях с: {formatDate(friendship.friend.createdAt)}
                  </p>
                )}
                {activeTab === "ACCEPTED" && (
                  <Link
                    to={`/chats/${friendship.friend.id}?type=private`}
                    style={{
                      display: "inline-block",
                      marginTop: "12px",
                      padding: "8px 16px",
                      backgroundColor: "#2196F3",
                      color: "white",
                      textDecoration: "none",
                      borderRadius: "4px",
                      fontSize: "14px",
                      fontWeight: "500",
                    }}
                  >
                    Написать сообщение
                  </Link>
                )}
                {activeTab === "INCOMING" && (
                  <div style={{ marginTop: "12px", display: "flex", gap: "8px" }}>
                    <button
                      onClick={() => respondToRequest(friendship.id, true)}
                      style={{
                        padding: "8px 16px",
                        backgroundColor: "#4CAF50",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        fontSize: "14px",
                        fontWeight: "500",
                        cursor: "pointer",
                        flex: 1,
                      }}
                    >
                      Принять
                    </button>
                    <button
                      onClick={() => respondToRequest(friendship.id, false)}
                      style={{
                        padding: "8px 16px",
                        backgroundColor: "#f44336",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        fontSize: "14px",
                        fontWeight: "500",
                        cursor: "pointer",
                        flex: 1,
                      }}
                    >
                      Отклонить
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default FriendsPage;