#import <UIKit/UIKit.h>
#import <sys/utsname.h>

#import <Flutter/Flutter.h>

@interface FlutterDeviceInfoPlugin : NSObject<FlutterPlugin>

@property (nonatomic) bool isEmulator;
@property (nonatomic) NSDictionary* constants;

@end
