export interface PubUser {
  id: number;
  login: string;
  role: string;
  created_at?: string;
  createdAt?: string;

  authorId?: number;

  author?: {
    authorId?: number;
    firstname: string;
    surname: string;
  };
}