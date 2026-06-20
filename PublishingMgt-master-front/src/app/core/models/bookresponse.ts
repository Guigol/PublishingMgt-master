export interface BookResponse {
  id: number;
  title: string;
  publisher: number;
  authors: {
    id: number;
    firstname: string;
    surname: string;
  }[];
}
