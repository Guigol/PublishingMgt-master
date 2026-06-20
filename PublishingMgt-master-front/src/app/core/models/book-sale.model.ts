import { Book } from './book.model';
import { Publishing } from './publishing.model';

export interface BookSale {
  id: number;

  bookId: number;
  publishingId: number;

  month: number;
  year: number;

  quantitySold: number;
  quantityReturn: number;
  averageDiscount: number;

  book?: Book;
  publishing?: Publishing;
}