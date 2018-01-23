//
//  RNShareFile.m
//  RNShareFile
//
//  Created by You on 2018/01/20
//  Copyright © 2018 Genii Services. All rights reserved.
//

#import "RNShareFile.h"

@implementation RNShareFile

@synthesize documentInteractionController;

RCT_EXPORT_MODULE();

- (UIViewController *)documentInteractionControllerViewControllerForShare:(UIDocumentInteractionController *)controller
{
	UIViewController *root = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
	return root;
}
- (UIView *)documentInteractionControllerViewForShare:(UIDocumentInteractionController *)controller
{
	UIViewController *root = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
	return root.view;
}
- (CGRect)documentInteractionControllerRectForShare:(UIDocumentInteractionController *)controller
{
	UIViewController *root = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
	return root.view.frame;
}



RCT_EXPORT_METHOD(share:(NSDictionary *)options
	resolver:(RCTPromiseResolveBlock)resolve
	rejecter:(RCTPromiseRejectBlock)reject)
{
	NSString *fileURL = [RCTConvert NSString:options[@"url"]];
	NSURL *url = [NSURL fileURLWithPath:fileURL];

	self.documentInteractionController = [UIDocumentInteractionController interactionControllerWithURL:url];

	self.documentInteractionController.delegate = self;
	self.documentInteractionController.URL = url;

	UIViewController *ctrl = [[[[UIApplication sharedApplication] delegate] window] rootViewController];

	dispatch_async(dispatch_get_main_queue(), ^{
		if(![self.documentInteractionController presentOpenInMenuFromRect:ctrl.view.bounds inView:ctrl.view animated:YES]){
			UIAlertView *alertView = [[UIAlertView alloc]initWithTitle:nil message:@"There are no installed apps that can open this file." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil, nil];
			[alertView show];
		}       
	});    
}

@end
