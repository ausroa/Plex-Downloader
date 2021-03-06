import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatTabsModule} from '@angular/material/tabs';
import {RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import { StoreModule } from "@ngrx/store";

import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {LoginComponent} from './login/login.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {NavbarComponent} from './navbar/navbar.component';
import {appRoutes} from './routes';
import {AlertifyService} from './_service/alertify.service';
import {LoginService} from './_service/login.service';
import {LibraryService} from './_service/library.service';
import {RestInterceptor} from "./util/rest-interceptor";
import {SettingsComponent} from './settings/settings.component';
import {AboutComponent} from './settings/about/about.component';
import {SearchComponent} from './search/search.component';
import {NgbModule, NgbPopoverModule} from "@ng-bootstrap/ng-bootstrap";
import {SeriesPanelComponent} from './search/series-panel/series-panel.component';
import {MediaCardComponent} from './home/media-card/media-card.component';
import {MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {LoadingComponent} from './util/loading/loading.component';
import {LoadingScreenInterceptor} from "./util/loading-interceptor";
import { LibraryComponent } from './library/library.component';
import { LibraryFilterPipe } from './library/library-filter.pipe';
import { LibrarySeriesFilterPipe } from './library/library-series-filter.pipe';
import { LibrarySearchCriteriaFilterPipe } from './library/library-search-criteria-filter.pipe';
import {LibrarySeriesSearchCriteriaFilterPipe} from "./library/library-series-search-criteria-filter.pipe";
import { MobileCardComponent } from './home/mobile-card/mobile-card.component';
import { SystemSettingsComponent } from './settings/system-settings/system-settings.component';
import {counterReducer} from "./+state/reducer";


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    NavbarComponent,
    SettingsComponent,
    AboutComponent,
    SearchComponent,
    SeriesPanelComponent,
    MediaCardComponent,
    LoadingComponent,
    LibraryComponent,
    LibraryFilterPipe,
    LibrarySeriesFilterPipe,
    LibrarySearchCriteriaFilterPipe,
    LibrarySeriesSearchCriteriaFilterPipe,
    MobileCardComponent,
    SystemSettingsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(appRoutes, {onSameUrlNavigation: 'reload'}),
    BrowserAnimationsModule,
    MatTabsModule,
    NgbModule,
    MatDialogModule,
    StoreModule.forRoot({ count: counterReducer })
  ],
  providers: [
    LoginService,
    AlertifyService,
    LibraryService,
    {
      provide: HTTP_INTERCEPTORS, useClass: RestInterceptor, multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS, useClass: LoadingScreenInterceptor, multi: true,
    },
    {
      provide: MatDialogRef,
      useValue: {}
    },
  ],
  bootstrap: [AppComponent],
  entryComponents: [
    SeriesPanelComponent,
  ]
})
export class AppModule {
}
