export interface AuthorParticipation {
  id: number;
  authorId: number;
  authorName: string;
  bookId: number;
  bookTitle: string;
  pctRateRoyalties: number;
}