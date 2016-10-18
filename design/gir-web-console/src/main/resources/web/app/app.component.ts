import {Component, OnInit} from 'angular2/core';
import {HeroService} from './hero.service';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/interval';
import 'rxjs/add/operator/withLatestFrom';
import 'rxjs/add/operator/share';
import 'angular2/src/facade/collection';

@Component({
	selector: 'my-app',
	template:`

	<div id="header">Session on node {{nodeData}}
	</div>

	<div id="screenshot"><img id="pic" src="{{imageData}}"></div>
	<br>
	<div id="footer">
	<input id="newNode" [(ngModel)]="newNodeData" placeholder='node URI' /> 
	<br>
	<button id="refresh" (mousedown)="updateScreenshot()" 
		[class.selected]="refreshing">REFRESH</button>
	
		</div>		
	`,
	inputs: ['newNodeData']
	         ,
	         styles:[`  
	                 #header {
	        	 background-color:black;
	         color:white;
	         text-align:left;
	         padding:5px;
	         margin-bottom: 20px;
	         }
	         #screenshot {
	        	 height:700px;
	         float:left;
	         padding:5px;
	         box-shadow: 10px 10px 5px grey;
	         margin-bottom: 20px;
	         }
	         #pic {
	        	 height:100%;

	         }
	         #footer {
	        	 background-color:white;
	         box-shadow: 10px 10px 5px grey;
	         color:black;
	         clear:both;
	         text-align:left;
	         padding:5px;
	         margin-bottom: 20px;
	         
	         }
	         #refresh{
	        	background-color: #CFD8DC !important;
	         	color: black;
	            padding:5px;
	         box-shadow: 3px 3px 2px grey;
	         margin: 10px;
	         }
	         #newNode{
	        	 color: black;
	            padding:2px;
	         margin: 10px;
	         }
	         .selected {
	        	 background-color: #C00000 !important;
	         color: white;
	         
	         }
	         `],
	         providers: [HeroService]
})
export class AppComponent implements OnInit {
	public blankImage = 'data:image/gif;base64,R0lGODlhAQABAAAAACwAAAAAAQABAAA=';
	public imageData = this.blankImage;
	public nodeData : string;  
	public newNodeData : string;
	public refreshing = false;

	constructor(private _heroService: HeroService) { 
		this._heroService.getNodeInfo().then(info => this.updateNd(info));
	}

	startNodePolling(){
		this.nodeTask = this._heroService.pollNodeInfo().subscribe(info => info.then(da => {this.updateNd(da);}));
	}
	startPicturePolling(){
		this.screenshotTask = this._heroService.pollScreeshot().subscribe( screenshot => screenshot.then(pic => this.updateScreeshot(pic)));		
	}

	updateScreenshot(){
		console.log("SDS" + this.refreshing);
		this.refreshing = true;
		this._heroService.getScreenshot().then(pic => this.updateScreeshot(pic));
	}

	updateNodeInfo(){
		this._heroService.getNodeInfo().then(info => {this.updateNd(info);this.updateScreenshot()});
	}
	ngOnInit() {

		this.updateNodeInfo();

		this.startNodePolling();
		this.startPicturePolling();
	}
	updateNd(newInfo){
		if (newInfo !== undefined){
			this.nodeData = newInfo;
		}
	}

	updateScreeshot(newPic){
		console.log("SDS" + this.refreshing);
		if (newPic !== undefined){
			this.imageData = newPic;
			this.refreshing = false;
		}
	}

	ngOnDestroy() {
		this.nodeTask.unsubscribe();
		this.screenshotTask.unsubscribe();
	}

}
