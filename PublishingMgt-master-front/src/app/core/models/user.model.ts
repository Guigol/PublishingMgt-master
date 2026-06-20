export interface User {
  id: number;
  login: string;
  role: string;
  created_at: string;
  author?: {
    firstname: string;
    surname: string;
  };
}