import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../services/dashboard-service';
import {
  ApexAxisChartSeries,
  ApexChart,
  ApexXAxis,
  ApexDataLabels,
  ApexTitleSubtitle,
  ApexFill,
  ApexTooltip,
  ApexStroke,
} from 'ng-apexcharts';
import { NgApexchartsModule } from 'ng-apexcharts';

import { CommonModule } from '@angular/common';

export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  dataLabels?: ApexDataLabels;
  title?: ApexTitleSubtitle;
  fill?: ApexFill;
  tooltip?: ApexTooltip;
  stroke?: ApexStroke;
};

interface OperationTypeData {
  DEBIT?: number;
  CREDIT?: number;
}
export type PieChartOptions = {
  series: number[];
  chart: ApexChart;
  labels: string[];
  title?: ApexTitleSubtitle;
};
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgApexchartsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  stats: any = {};
  statCards: Array<{ label: string; value: any }> = [];
  allChartsReady: boolean = false;

  pieChartOptions: Partial<PieChartOptions> | any;
  barChartOperationsOptions: Partial<ChartOptions> | any;
  barChartCustomersOptions: Partial<ChartOptions> | any;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    let chartsLoaded = 0;

    const checkAllReady = () => {
      chartsLoaded++;
      if (chartsLoaded === 3) {
        this.allChartsReady = true;
      }
    };
    this.dashboardService.getAccountsStats().subscribe((data) => {
      this.stats = data;

      this.statCards = [
        { label: 'Customer Count', value: data.customerCount },
        { label: 'Account Count', value: data.accountCount },
        { label: 'Total Balance', value: data.totalBalance.toFixed(2) },
        { label: 'Operation Count', value: data.operationCount },
      ];

      this.pieChartOptions = {
        series: [data.currentAccounts, data.savingAccounts],
        chart: { type: 'pie', with: '100%' },
        labels: ['Current Accounts', 'Saving Accounts'],
        title: { text: 'Accounts Distribution' },
      };
      checkAllReady();
    });

    this.dashboardService
      .getOperationsByType()
      .subscribe((opData: OperationTypeData) => {
        this.barChartOperationsOptions = {
          series: [
            {
              name: 'Operations',
              data: [opData.DEBIT || 0, opData.CREDIT || 0],
            },
          ],
          chart: { type: 'bar', height: 350 },
          xaxis: { categories: ['DEBIT', 'CREDIT'] },
          title: { text: 'Operations by Type' },
        };
        checkAllReady();
      });

    this.dashboardService.getMostActiveCustomers().subscribe((data) => {
      this.barChartCustomersOptions = {
        series: [
          {
            name: 'Operations per Customer',
            data: Object.values(data),
          },
        ],
        chart: { type: 'bar', height: 350 },
        xaxis: { categories: Object.keys(data) },
        title: { text: 'Active Customers' },
      };
      checkAllReady();
    });
  }
}
