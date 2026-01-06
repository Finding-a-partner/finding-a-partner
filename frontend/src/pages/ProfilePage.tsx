import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { userApi } from '../api/userApi';
import { useAuth } from "../context/AuthContext";
import {User} from "../types"


const ProfilePage = () => {
  const [user_, setUser_] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { id } = useParams();
  const { setUser, setToken } = useAuth();

  const { user, token } = useAuth();
  const currentUserId = user?.id;

  const isOwnProfile = currentUserId === Number(id);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          navigate('/login');
          return;
        }

        if (!id) {
          setError('Не удалось определить пользователя');
          return;
        }

        const userData = await userApi.getUserById(Number(id), token);
        setUser_(userData);
      } catch (err) {
        setError('Ошибка загрузки данных пользователя');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [id, navigate]);

  const handleLogout = () => {
    setUser(null);
    setToken(null);
    navigate('/login');
  };

  const getCurrentUserId = (): number => {
    const userJson = localStorage.getItem("user");
    const user_ = userJson ? JSON.parse(userJson) : null;
    return user_.id
  };

  if (loading) return <div>Загрузка...</div>;
  if (error) return <div>{error}</div>;
  if (!user_) return <div>Пользователь не найден</div>;

  return (
    <div className="profile-container">
      <h1>{isOwnProfile ? 'Личный профиль' : 'Профиль пользователя'}</h1>

      {isOwnProfile && (
        <button onClick={handleLogout}>Выйти</button>
      )}

      <div className="profile-info">
        <h2>{user_.name} {user_.surname}</h2>
        <p>Логин: {user_.login}</p>
        <p>Email: {user_.email}</p>
        <p>Описание: {user_.description}</p>
      </div>

      <div className="navigation-buttons" style={{ marginTop: '20px', display: 'flex', gap: '10px' }}>
        {isOwnProfile ? (
          <>
            <button onClick={() => navigate('/friends')}>Друзья</button>
            <button onClick={() => navigate('/groups')}>Группы</button>
            <button onClick={() => navigate('/events')}>Мероприятия</button>
            <button onClick={() => navigate('/chats')}>Чаты</button>
          </>
        ) : (
          <>
            <button onClick={() => userApi.makeFriendRequest({userId: getCurrentUserId(), friendId: Number(id), status: "PENDING"}, token)}>Добавить в друзья</button>
            <button onClick={() => navigate(`/chats/${user_.id}`)}>Написать сообщение</button>
          </>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;
