export interface Publishing {
  publishingId: number;
  name: string;
  isbn: string;
  noTprice: number;
  royalties: number;
  book: {
    book_id: number;
    title?: string;
  };
}