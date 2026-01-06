import React, { useState, FormEvent } from "react";

interface AuthFormProps {
  title: string;
  onSubmit: (email: string, password: string) => void;
}

const AuthForm: React.FC<AuthFormProps> = ({ title, onSubmit }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setError("");

    if (!email || !password) {
      setError("Все поля обязательны");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError("Некорректный email");
      return;
    }

    if (password.length < 6) {
      setError("Пароль должен содержать минимум 6 символов");
      return;
    }

    onSubmit(email, password);
  };

  return (
    <form onSubmit={handleSubmit} className="max-w-sm mx-auto p-4 border rounded shadow">
      <h2 className="text-2xl font-bold mb-4">{title}</h2>

      {error && <div className="text-red-500 mb-2">{error}</div>}

      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        className="w-full p-2 border rounded mb-3"
      />

      <input
        type="password"
        placeholder="Пароль"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="w-full p-2 border rounded mb-3"
      />

      <button type="submit" className="w-full bg-blue-600 text-white p-2 rounded">
        {title}
      </button>
    </form>
  );
};

export default AuthForm;
