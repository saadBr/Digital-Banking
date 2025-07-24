import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-log-viewer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './log-viewer.html',
})
export class LogViewer implements OnInit {
  logs: any[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  usernameFilter: string = '';
  actionFilter: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchLogs();
  }

  fetchLogs() {
    this.loading = true;
    this.errorMessage = '';

    let params = new HttpParams();
    if (this.usernameFilter) {
      params = params.set('username', this.usernameFilter);
    }
    if (this.actionFilter) {
      params = params.set('action', this.actionFilter);
    }

    this.http
      .get<any[]>(`${environment.backendHost}/logs/search`, { params })
      .subscribe({
        next: (data) => {
          this.logs = data;
          this.loading = false;
        },
        error: (err) => {
          this.errorMessage = 'Failed to load logs.';
          this.logs = [];
          this.loading = false;
          console.error(err);
        },
      });
  }

  fetchFilteredLogs() {
    this.fetchLogs();
  }

  resetFilters() {
    this.usernameFilter = '';
    this.actionFilter = '';
    this.fetchLogs();
  }
}
