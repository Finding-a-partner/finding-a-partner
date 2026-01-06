import { useEffect, useState } from "react";
import { userApi } from "../api/userApi";
import UserCard from "../components/UserCard";
import { useAuth } from "../context/AuthContext";

type User = {
  id: number;
  name: string;
  surname: string;
  login: string;
  email: string;
  description?: string;
};

const UserSearchPage = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [query, setQuery] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { user, token } = useAuth();

  const fetchUsers = async (searchQuery?: string) => {
    try {
      setLoading(true);
      setError("");

      const data = searchQuery?.trim()
        ? await userApi.searchUsers(searchQuery, token)
        : await userApi.getAllUsers(token);

      setUsers(data);
    } catch (err: any) {
      setError(err.message || "Ошибка загрузки пользователей");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleSearch = () => {
    fetchUsers(query);
  };

  return (
    <div>
      <h2>Поиск пользователей</h2>
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Введите имя, логин и т.п."
      />
      <button onClick={handleSearch}>Поиск</button>

      {error && <p style={{ color: "red" }}>{error}</p>}
      {loading && <p>Загрузка...</p>}

      <div style={{ marginTop: "20px" }}>
        {users.length === 0 && !loading ? (
          <p>Нет пользователей</p>
        ) : (
          users.map((user) => <UserCard key={user.id} user={user} />)
        )}
      </div>
    </div>
  );
};

export default UserSearchPage;
