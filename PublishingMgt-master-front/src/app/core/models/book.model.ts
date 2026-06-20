export interface Book {
  id: number;
  title: string;
  publisher: number;
  authors: {
    id: number;
    firstname: string;
    surname: string;
  }[];
}