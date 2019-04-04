cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
  {
    "id": "cordova-plugin-toggle-screen.ScreenToggle",
    "file": "plugins/cordova-plugin-toggle-screen/www/ScreenToggle.js",
    "pluginId": "cordova-plugin-toggle-screen",
    "clobbers": [
      "cordova.plugins.ScreenToggle"
    ]
  }
];
module.exports.metadata = 
// TOP OF METADATA
{
  "cordova-plugin-toggle-screen": "1.0.0",
  "cordova-plugin-whitelist": "1.3.3"
};
// BOTTOM OF METADATA
});