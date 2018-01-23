# React Native Share File

This package calls UIDocumentInteractionController for IOS to share the file.

```js
import RNShareFile from 'react-native-file-share'

RNShareFile.share({
    url:localFilePath
})
```

To share a remote file, download with `react-native-fs`.