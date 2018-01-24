# React Native Share File

Share with a file for React Native
This package calls UIDocumentInteractionController for IOS to share the file.

```js
import RNShareFile from 'react-native-file-share'

RNShareFile.share({
    url:localFilePath
})
```

To share a remote file, download with `react-native-fs`.