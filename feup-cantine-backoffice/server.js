const express = require("express");
const path = require("path");
const proxy = require("http-proxy-middleware");

const app = express();
app.use(express.static(__dirname + "/dist"));

// add middleware for http proxying
const apiProxy = proxy.createProxyMiddleware("/api", {
  changeOrigin: true,
  target: "https://feup-food.herokuapp.com",
});
app.use("/api", apiProxy);

app.get("/*", function (req, res) {
  res.sendFile(path.join(__dirname + "/dist/index.html"));
});

app.listen(process.env.PORT || 8020);
