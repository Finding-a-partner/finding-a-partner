import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const RegisterPage = () => {
  const [form, setForm] = useState({
    login: "",
    email: "",
    password: "",
    name: "",
    surname: "",
    description: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();
  const { setUser } = useAuth();
  const { setToken } = useAuth();

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleRegister = async () => {
    try {
      const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
      const response = await fetch(`${API_URL}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Ошибка регистрации");
      }

      const data = await response.json();

      setToken(data.accessToken); // сохраняем токен
      setUser(data.user)

      setSuccess(true);
      setTimeout(() => {
        navigate("/feed");
      }, 1500); // через 1.5 секунды переходим
    } catch (err: any) {
      setError(err.message || "Ошибка регистрации");
    }
  };

  return (
    <div>
      <h2>Регистрация</h2>
      <input name="login" placeholder="Логин" value={form.login} onChange={handleChange} />
      <input name="email" placeholder="Email" value={form.email} onChange={handleChange} />
      <input name="password" type="password" placeholder="Пароль" value={form.password} onChange={handleChange} />
      <input name="name" placeholder="Имя" value={form.name} onChange={handleChange} />
      <input name="surname" placeholder="Фамилия" value={form.surname} onChange={handleChange} />
      <textarea name="description" placeholder="Описание" value={form.description} onChange={handleChange} />
      <button onClick={handleRegister}>Зарегистрироваться</button>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {success && <p style={{ color: "green" }}>Регистрация прошла успешно!</p>}
    </div>
  );
};

export default RegisterPage;
