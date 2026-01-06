import React, { createContext, useContext, useEffect, useState } from "react";
import {User} from "../types"

type AuthContextType = {
  user: User | null;
  setUser: (user: User | null) => void;
  token: string | null;
  setToken: (token: string | null) => void;
};

const AuthContext = createContext<AuthContextType>({
  user: null,
  setUser: () => {},
  token: null,
  setToken: () => {},
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    try {
      const stored = localStorage.getItem("user");
      return stored ? JSON.parse(stored) : null;
    } catch (err) {
      console.warn("Ошибка при чтении user из localStorage", err);
      localStorage.removeItem("user");
      return null;
    }
  });
  

  const [token, setToken] = useState<string | null>(() => {
    try {
      return localStorage.getItem("token");
    } catch (err) {
      console.warn("Ошибка при чтении token из localStorage", err);
      localStorage.removeItem("token");
      return null;
    }
  });

  useEffect(() => {

    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user");
    }
  }, [user]);

  useEffect(() => {
    console.log("TOKEN CHANGED:", token); 
    if (token) {
      localStorage.setItem("token", token);
    } else {
      localStorage.removeItem("token");
    }
  }, [token]);

  return (
    <AuthContext.Provider value={{ user, setUser, token, setToken }}>
      {children}
    </AuthContext.Provider>
  );
};
