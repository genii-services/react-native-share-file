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

	NSString *deviceModel = (NSString*)[UIDevice currentDevice].model;
	if ([[deviceModel substringWithRange:NSMakeRange(0, 4)] isEqualToString:@"iPad"]) {
		//printf("iPad");
		CGRect rect = CGRectMake (0.0, 0.0, 0.0, 0.0);
		dispatch_async(dispatch_get_main_queue(), ^{
			[self.documentInteractionController presentOpenInMenuFromRect:rect inView:ctrl.view animated:YES];
		});    
	} else {
		//printf("iPhone or iPod Touch");
		dispatch_async(dispatch_get_main_queue(), ^{
			[self.documentInteractionController presentOpenInMenuFromRect:ctrl.view.bounds inView:ctrl.view animated:YES];       
		});    
	}
}

@end
