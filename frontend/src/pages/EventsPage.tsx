import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import eventApi from '../api/eventsApi';
import { Owner, Group, Visibility } from '../types';

type OwnerType = 'USER' | 'GROUP';
type EventTab = 'participating' | 'created';

interface EventResponse {
  id: number;
  ownerId: number;
  ownerType: OwnerType;
  title: string;
  description: string | null;
  time: string;
  date: string;
  visibility: Visibility;
}

const EventsPage = () => {
  const [events, setEvents] = useState<EventResponse[]>([]);
  const [ownerInfo, setOwnerInfo] = useState<Record<string, Owner | Group>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<EventTab>('participating');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    date: '',
    time: '',
    visibility: Visibility.EVERYONE,
  });
  const [formErrors, setFormErrors] = useState<Record<string, string>>({});
  const navigate = useNavigate();

  useEffect(() => {
    fetchEvents();
  }, [navigate, activeTab]);

  const fetchEvents = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }

      setLoading(true);
      let eventsData: EventResponse[];
      
      if (activeTab === 'participating') {
        eventsData = await eventApi.getUserEvents();
      } else {
        eventsData = await eventApi.getOwnerEvents();
      }

      setEvents(eventsData);
      
      const ownersData: Record<string, Owner | Group> = {};
      for (const event of eventsData) {
        const key = `${event.ownerType}_${event.ownerId}`;
        if (!ownerInfo[key]) {
          ownersData[key] = await eventApi.getOwnerInfo(event.ownerId, event.ownerType);
        }
      }
      
      setOwnerInfo(prev => ({ ...prev, ...ownersData }));
    } catch (err) {
      setError('Ошибка загрузки мероприятий');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    if (formErrors[name]) {
      setFormErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};
    
    if (!formData.title.trim()) {
      errors.title = 'Название обязательно';
    }
    
    if (!formData.date) {
      errors.date = 'Дата обязательна';
    }
    
    if (!formData.time) {
      errors.time = 'Время обязательно';
    }
    
    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    try {
      await eventApi.createEvent({
        title: formData.title,
        description: formData.description || undefined,
        visibility: formData.visibility,
        date: formData.date,
        time: formData.time
      });
      
      setShowCreateForm(false);
      setFormData({
        title: '',
        description: '',
        date: '',
        time: '',
        visibility: Visibility.EVERYONE,
      });
      
      await fetchEvents();
    } catch (err) {
      setError('Ошибка при создании мероприятия');
      console.error(err);
    }
  };

  const formatDateTime = (dateStr: string, timeStr: string) => {
    const date = new Date(`${dateStr}T${timeStr}`);
    return date.toLocaleString('ru-RU', {
      weekday: 'short',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const renderVisibilityLabel = (visibility: Visibility) => {
    switch (visibility) {
      case Visibility.EVERYONE:
        return 'Для всех';
      case Visibility.FRIENDS:
        return 'Только для друзей';
      case Visibility.GROUP:
        return 'Только для группы';
      default:
        return visibility;
    }
  };

  const renderOwnerInfo = (ownerId: number, ownerType: OwnerType) => {
    const key = `${ownerType}_${ownerId}`;
    const owner = ownerInfo[key];
    
    if (!owner) return <span>Загрузка...</span>;
    
    if (ownerType === 'USER') {
      const userOwner = owner as Owner;
      return (
        <span>
          {userOwner.name} {userOwner.surname} (@{userOwner.login})
        </span>
      );
    } else {
      const groupOwner = owner as Group;
      return (
        <span>
          Группа {groupOwner.name}
        </span>
      );
    }
  };

  const handleVisibilityChange = (vis: Visibility) => {
    setFormData(prev => ({
      ...prev,
      visibility: vis
    }));
  };

  const handleEditEvent = (event: EventResponse) => {
    setFormData({
      title: event.title,
      description: event.description || '',
      date: event.date,
      time: event.time,
      visibility: event.visibility
    });
    setShowCreateForm(true);
  };
  
  const handleDeleteEvent = async (eventId: number) => {
    if (window.confirm('Вы уверены, что хотите удалить это мероприятие?')) {
      try {
        await eventApi.deleteEvent(eventId);
        await fetchEvents();
      } catch (err) {
        setError('Ошибка при удалении мероприятия');
        console.error(err);
      }
    }
  };

  if (loading) return <div>Загрузка мероприятий...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div className="events-container" style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h1>Мои мероприятия</h1>
      
      <div style={{ display: 'flex', marginBottom: '20px', borderBottom: '1px solid #e0e0e0' }}>
        <button
          onClick={() => setActiveTab('participating')}
          style={{
            padding: '10px 20px',
            backgroundColor: activeTab === 'participating' ? '#f0f0f0' : 'transparent',
            border: 'none',
            borderBottom: activeTab === 'participating' ? '2px solid #1976d2' : 'none',
            cursor: 'pointer',
            fontWeight: activeTab === 'participating' ? 'bold' : 'normal'
          }}
        >
          Участвую
        </button>
        <button
          onClick={() => setActiveTab('created')}
          style={{
            padding: '10px 20px',
            backgroundColor: activeTab === 'created' ? '#f0f0f0' : 'transparent',
            border: 'none',
            borderBottom: activeTab === 'created' ? '2px solid #1976d2' : 'none',
            cursor: 'pointer',
            fontWeight: activeTab === 'created' ? 'bold' : 'normal'
          }}
        >
          Организую
        </button>
      </div>

      {activeTab === 'created' && (
        <button 
          onClick={() => setShowCreateForm(true)}
          style={{
            marginBottom: '20px',
            padding: '10px 20px',
            backgroundColor: '#1976d2',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontWeight: 'bold'
          }}
        >
          Создать мероприятие
        </button>
      )}

      <button 
        onClick={() => navigate(-1)} 
        style={{ 
          marginBottom: '20px',
          padding: '8px 16px',
          backgroundColor: '#f0f0f0',
          border: '1px solid #ddd',
          borderRadius: '4px',
          cursor: 'pointer'
        }}
      >
        Назад
      </button>
      
      {showCreateForm && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: 'white',
            padding: '20px',
            borderRadius: '8px',
            width: '100%',
            maxWidth: '500px'
          }}>
            <h2 style={{ marginTop: 0 }}>Создать мероприятие</h2>
            <form onSubmit={handleSubmit}>
              <div style={{ marginBottom: '15px' }}>
                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  Название *
                </label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  style={{
                    width: '100%',
                    padding: '8px',
                    border: `1px solid ${formErrors.title ? 'red' : '#ddd'}`,
                    borderRadius: '4px'
                  }}
                />
                {formErrors.title && (
                  <span style={{ color: 'red', fontSize: '0.8em' }}>{formErrors.title}</span>
                )}
              </div>
              
              <div style={{ marginBottom: '15px' }}>
                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  Описание
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  style={{
                    width: '100%',
                    padding: '8px',
                    border: '1px solid #ddd',
                    borderRadius: '4px',
                    minHeight: '80px'
                  }}
                />
              </div>
              
              <div style={{ marginBottom: '15px' }}>
                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  Дата *
                </label>
                <input
                  type="date"
                  name="date"
                  value={formData.date}
                  onChange={handleInputChange}
                  style={{
                    width: '100%',
                    padding: '8px',
                    border: `1px solid ${formErrors.date ? 'red' : '#ddd'}`,
                    borderRadius: '4px'
                  }}
                />
                {formErrors.date && (
                  <span style={{ color: 'red', fontSize: '0.8em' }}>{formErrors.date}</span>
                )}
              </div>
              
              <div style={{ marginBottom: '15px' }}>
                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  Время *
                </label>
                <input
                  type="time"
                  name="time"
                  value={formData.time}
                  onChange={handleInputChange}
                  style={{
                    width: '100%',
                    padding: '8px',
                    border: `1px solid ${formErrors.time ? 'red' : '#ddd'}`,
                    borderRadius: '4px'
                  }}
                />
                {formErrors.time && (
                  <span style={{ color: 'red', fontSize: '0.8em' }}>{formErrors.time}</span>
                )}
              </div>
              
              <div style={{ marginBottom: '20px' }}>
                <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                  Видимость
                </label>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {Object.values(Visibility).map((vis) => (
                    <label key={vis} style={{ display: 'flex', alignItems: 'center' }}>
                      <input
                        type="radio"
                        name="visibility"
                        value={vis}
                        checked={formData.visibility === vis}
                        onChange={() => handleVisibilityChange(vis)}
                        style={{ marginRight: '8px' }}
                      />
                      {renderVisibilityLabel(vis)}
                    </label>
                  ))}
                </div>
              </div>
              
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
                <button
                  type="button"
                  onClick={() => setShowCreateForm(false)}
                  style={{
                    padding: '8px 16px',
                    backgroundColor: '#f0f0f0',
                    border: '1px solid #ddd',
                    borderRadius: '4px',
                    cursor: 'pointer'
                  }}
                >
                  Отмена
                </button>
                <button
                  type="submit"
                  style={{
                    padding: '8px 16px',
                    backgroundColor: '#1976d2',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontWeight: 'bold'
                  }}
                >
                  Создать
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
      
      {events.length === 0 ? (
        <p style={{ color: '#666' }}>
          {activeTab === 'participating' 
            ? 'Вы пока не участвуете ни в каких мероприятиях' 
            : 'Вы пока не создали ни одного мероприятия'}
        </p>
      ) : (
        <div className="events-list">
          {events.map(event => (
            <div 
              key={event.id} 
              className="event-card" 
              style={{
                border: '1px solid #e0e0e0',
                borderRadius: '8px',
                padding: '16px',
                marginBottom: '16px',
                cursor: 'pointer',
                transition: 'all 0.3s ease',
                backgroundColor: '#fff',
                boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
                position: 'relative'
              }}
            >
              {activeTab === 'created' && (
                <div style={{
                  position: 'absolute',
                  top: '10px',
                  right: '10px',
                  display: 'flex',
                  gap: '8px'
                }}>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleEditEvent(event);
                    }}
                    style={{
                      padding: '4px 8px',
                      backgroundColor: '#1976d2',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer'
                    }}
                  >
                    Редактировать
                  </button>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDeleteEvent(event.id);
                    }}
                    style={{
                      padding: '4px 8px',
                      backgroundColor: '#f44336',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer'
                    }}
                  >
                    Удалить
                  </button>
                </div>
              )}
              <div 
                style={{ cursor: 'pointer' }}
                onClick={() => navigate(`/events/${event.id}`)}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <h3 style={{ margin: '0', color: '#333' }}>{event.title}</h3>
                  <span style={{ 
                    color: '#666',
                    fontSize: '0.9em',
                    backgroundColor: '#f5f5f5',
                    padding: '4px 8px',
                    borderRadius: '4px'
                  }}>
                    {formatDateTime(event.date, event.time)}
                  </span>
                </div>
                
                <div style={{ marginTop: '8px' }}>
                  <div style={{ fontWeight: 'bold', marginBottom: '4px', color: '#555' }}>Владелец:</div>
                  {renderOwnerInfo(event.ownerId, event.ownerType)}
                </div>
                
                <div style={{ marginTop: '8px' }}>
                  <div style={{ fontWeight: 'bold', marginBottom: '4px', color: '#555' }}>Видимость:</div>
                  {renderVisibilityLabel(event.visibility)}
                </div>
                
                {event.description && (
                  <>
                    <div style={{ fontWeight: 'bold', margin: '8px 0 4px', color: '#555' }}>Описание:</div>
                    <p style={{ 
                      marginTop: '4px', 
                      color: '#555',
                      fontSize: '0.95em'
                    }}>
                      {event.description}
                    </p>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default EventsPage;