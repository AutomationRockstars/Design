import {Injectable} from 'angular2/core';
import {Http, Headers, HTTP_PROVIDERS} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/interval';
import 'rxjs/add/operator/withLatestFrom';
import 'rxjs/add/operator/share';
import 'rxjs/add/operator/timeout';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
@Injectable()
export class HeroService {

	constructor(public http: Http){
		this.http = http;
		this.ip;
		this.port;
	}



	getScreenshot(){
		return new Promise(resolve =>
		this.http.get('http://localhost:8181/screenshot&format=base64')
		.toPromise()
		.then(pic => {
			if (pic.status == 200)
			resolve(pic.text())
			else resolve(undefined)})));
	}

	getNodeInfo(){
		return new Promise(resolve =>{
			this.http.get('http://localhost:8181/config/node.ip')
			.subscribe( res => {
				this.ip = res.text();
				this.http.get('http://localhost:8181/config/node.port')
				.subscribe( res2 => {
					this.port = res2.text();
					resolve("http://"+this.ip+":"+this.port);
				});
			});
		});
	}



	askForNodeUpdate(param){
		return this.http.head('http://localhost:8181/config/node.'+param)
		.map(res =>  res.headers.get("X-TIMEOUT"))
	}

	log(de){
		for (var a in de){
			console.log("P: " a + " of " + typeof a);
		}
	}
	pollInfo(type,callback){
		return Observable.interval(4950)
		.map(val => this.askForNodeUpdate(type).toPromise())
		.map(header => {		
			return new Promise( check => {
			header.then( h => {
				if (h){
					check(undefined);
				}else {
					if (callback == this.getNodeInfo){
						this.getNodeInfo().then( ip => check(ip));
					} else if (callback == this.getScreenshot){
						this.getScreenshot().then( ip => check(ip));
					}
				}
			}
			)});
		});
	}
	pollScreeshot(){
		return this.pollInfo("screenshot",this.getScreenshot);
	}
	pollNodeInfo(){
		return this.pollInfo("ip",this.getNodeInfo);
	}

}
