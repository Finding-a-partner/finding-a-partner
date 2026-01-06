interface JwtPayload {
    sub: string;       // username
    userId: string;    // из claim
    iat: number;       // issued at
    exp: number;       // expiration time
    [key: string]: any; // другие возможные поля
  }
  
  export const decodeJwt = (token: string): JwtPayload | null => {
    try {
        
      // Проверяем наличие токена и базовую структуру
      if (!token || typeof token !== 'string') {
        throw new Error('Token is not a valid string');
      }
  
      // Удаляем возможные префиксы (например, 'Bearer ')
      const cleanToken = token.replace(/^Bearer\s+/i, '');
  
      // Разбиваем токен на части
      const parts = cleanToken.split('.');
      if (parts.length !== 3) {
        throw new Error('Invalid token format: expected 3 parts');
      }
  
      // Декодируем payload из base64url
      const payload = parts[1];
      const decodedPayload = decodeBase64Url(payload);
      
      // Парсим JSON
      const parsed: JwtPayload = JSON.parse(decodedPayload);
      
      // Проверяем обязательные поля
      if (!parsed.sub) {
        throw new Error('Token missing "sub" field');
      }
  
      // Проверяем userId (может быть в разных полях в зависимости от сервера)
      if (!parsed.userId && !parsed.sub) {
        throw new Error('Token missing user identification');
      }
  
      // Проверяем срок действия
      if (parsed.exp && parsed.exp < Date.now() / 1000) {
        throw new Error('Token expired');
      }
      
      return parsed;
    } catch (error) {
      console.error('JWT decode error:', error);
      return null;
    }
  };
  
  // Функция для декодирования base64url
  const decodeBase64Url = (str: string): string => {
    let output = str.replace(/-/g, '+').replace(/_/g, '/');
    switch (output.length % 4) {
      case 0: break;
      case 2: output += '=='; break;
      case 3: output += '='; break;
      default: throw new Error('Illegal base64url string');
    }
  
    try {
      return decodeURIComponent(atob(output).split('').map(c => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
    } catch (err) {
      return atob(output);
    }
  };
  
  // Получение ID пользователя с обработкой разных случаев
  export const getCurrentUserId = (): string => {
    const token = localStorage.getItem('token');
    if (!token) throw new Error('No token found');

    
    
    const decoded = decodeJwt(token);
    if (!decoded) throw new Error('Invalid token');
  
    // Проверяем разные возможные места, где может быть userId
    const userId = decoded.userId || decoded.sub || decoded.nameid;
    if (!userId) throw new Error('User ID not found in token');
    
    return userId.toString(); // На случай, если userId число
  };