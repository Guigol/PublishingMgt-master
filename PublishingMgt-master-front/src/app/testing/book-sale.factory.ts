import { BookSale } from '../core/models/book-sale.model';
import { createBook } from './book.factory';

export function createBookSale(overrides: Partial<BookSale> = {}): BookSale {
  return {
    id: 1,
    bookId: 1,
    publishingId: 1,
    year: 2025,
    month: 1,
    quantitySold: 10,
    quantityReturn: 0,
    averageDiscount: 0,
    book: createBook(),
    ...overrides
  };
}