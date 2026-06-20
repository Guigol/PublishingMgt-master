import { Book } from '../core/models/book.model';

export function createBook(overrides: Partial<Book> = {}): Book {
  return {
    id: 1,
    title: 'Test Book',
    publisher: 1, 
    authors: [],
    ...overrides
  };
}