import { User } from '../types';
import { useNavigate } from 'react-router-dom';

interface UserCardProps {
  user: User;
}

const UserCard = ({ user }: UserCardProps) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/users/${user.id}`); // переход на страницу профиля
  };

  return (
    <div 
      onClick={handleClick}
      style={{ 
        border: '1px solid #ccc', 
        padding: '10px', 
        marginBottom: '10px', 
        cursor: 'pointer',
        borderRadius: '6px',
        transition: 'background 0.2s',
      }}
      onMouseEnter={(e) => (e.currentTarget.style.backgroundColor = '#f9f9f9')}
      onMouseLeave={(e) => (e.currentTarget.style.backgroundColor = 'white')}
    >
      <strong>{user.name} {user.surname}</strong> ({user.login})<br />
      <small>{user.email}</small><br />
      {user.description && <p>{user.description}</p>}
    </div>
  );
};

export default UserCard;
