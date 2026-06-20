import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { RoyaltiesService } from '../../core/services/royalties.service';
import { Royalty } from '../../core/models/royalty.model';

@Component({
  selector: 'app-admmgr-royalties',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admmgr-royalties.component.html',
   styleUrls: ['./admmgr-royalties.component.css']
})
export class ManagerRoyaltiesComponent implements OnInit {

  royalties: Royalty[] = [];

  authors: any[] = [];
  books: any[] = [];

  selectedAuthorId: number | null = null;
  selectedBookId: number | null = null;

  pageIndex: number = 0;
  pageSize: number = 5;

  constructor(
    private royaltyService: RoyaltiesService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAuthors();
  }

  // ---------------------------
  // Load authors
  // ---------------------------
  loadAuthors() {
      this.authService.getAuthors()
      .subscribe(data => {
        this.authors = data;
        this.cdr.detectChanges();
      });
  }

  // ---------------------------
  // Load books by author
  // ---------------------------
  loadBooksByAuthor(authorId: number) {
  
  if (!authorId) return;

  this.royaltyService.getBooksByAuthor(authorId)
    .subscribe(data => {
      this.books = data;  
      this.cdr.detectChanges();
    });
}

  // ---------------------------
  // changed author
  // ---------------------------
  onAuthorChange(authorId: number | null) {
  
  if (!authorId) return;

  this.selectedBookId = null;
  this.books = [];
  this.royalties = [];

  this.loadBooksByAuthor(authorId);
}
  // ---------------------------
  // changed book
  // ---------------------------
  onBookChange() {
    if (!this.selectedBookId) return;
    this.loadByBook(this.selectedBookId);
  }

  // ---------------------------
  // Royalties by book
  // ---------------------------
  loadByBook(bookId: number) {
    this.royaltyService.getRoyaltiesByBook(bookId)
      .subscribe(data => {
       
        this.royalties = data;
        this.pageIndex = 0;
        this.cdr.detectChanges();
      });
  }

 // ---------------------------
  // Paginator
  // ---------------------------

  get totalPages(): number {
  return Math.ceil(this.royalties.length / this.pageSize) || 1;
}

nextPage() {
  if (this.pageIndex < this.totalPages - 1) {
    this.pageIndex++;
  }
}

prevPage() {
  if (this.pageIndex > 0) {
    this.pageIndex--;
  }
}

}