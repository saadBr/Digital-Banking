import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ChartConfiguration, ChartType } from 'chart.js';
import { environment } from '../../environments/environment';
import { BaseChartDirective, NgChartsModule } from 'ng2-charts';
import { DashboardStats } from '../model/dashboard.model';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../services/dashboard-service';

@Component({
  selector: 'app-dashboard',
  imports: [NgChartsModule,CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  
  stats: any = {};
  statCards:any ={};

  pieChartData : ChartConfiguration<'pie'>['data'] = {
    labels: ['Current Accounts', 'Saving Accounts'],
    datasets: [{data: [0,0],
      backgroundColor: ['#4e73df', '#1cc88a'], 
      hoverBackgroundColor: ['#2e59d9', '#17a673'],
      borderWidth: 1
    }]
  };
  chartType: ChartType = 'pie';

  barChartData: ChartConfiguration<'bar'>['data'] = {
  labels: ['DEBIT', 'CREDIT', 'TRANSFER'],
  datasets: [{ data: [0, 0, 0], label: 'Operations',backgroundColor: ['#4e73df'], 
      hoverBackgroundColor: ['#2e59d9']
      }]
};

barChartMostActiveData: ChartConfiguration<'bar'>['data'] = {
  labels: [],
  datasets: [
    { data: [], label: 'Operations per Customer', backgroundColor: ['#1cc88a'], 
      hoverBackgroundColor: ['#17a673'] }
  ]
};

barChartType: ChartType = 'bar';
  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    
    this.dashboardService.getAccountsStats().subscribe(data => {
    this.stats = data;
    this.pieChartData.datasets[0].data = [
      data.currentAccounts, data.savingAccounts
    ];
    this.statCards = [
    { label: 'Customer Count', value: this.stats.customerCount },
    { label: 'Account Count', value: this.stats.accountCount },
    { label: 'Total Balance', value: this.stats.totalBalance.toFixed(2) },
    { label: 'Operation Count', value: this.stats.operationCount }
    ];
    setTimeout(() => this.chart?.update(), 0);
  });

  this.dashboardService.getOperationsByType().subscribe((opData: any) => {
    this.barChartData.datasets[0].data = [
      opData.DEBIT || 0,
      opData.CREDIT || 0,
      opData.TRANSFER || 0
    ];
    this.barChartData.labels = ['DEBIT', 'CREDIT', 'TRANSFER'];
    setTimeout(() => this.chart?.update(), 0);
  });
  
  this.dashboardService.getMostActiveCustomers().subscribe((data: any) => {
  this.barChartMostActiveData.labels = Object.keys(data);
  this.barChartMostActiveData.datasets[0].data = Object.values(data);
  setTimeout(() => this.chart?.update(), 0);
  });
  
  }
}
