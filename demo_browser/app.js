const https = require("https");
const express = require('express')
const fs = require("fs");
const app = express()
app.use(express.json({
  limit : "100mb"
}));

const port = 3000

const jsonfile = '/data.json'

const options = {
  method: 'POST',
  key: fs.readFileSync("./cert.key"),
  cert: fs.readFileSync("./cert.crt"),
};

app.get('/', (req, res) => {
  res.sendFile(__dirname + '/sample.html');
});

app.get('/data', (req, res) => {
  console.log('data sendfile');
  res.sendFile(__dirname + jsonfile);
});

app.post('/update-graph', (req, res) => {
  const graphdata = req.body;
  fs.writeFileSync('./data.json', JSON.stringify(graphdata, null, 2));
  res.json({status: 'ok'});
});

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})

https.createServer(options, app).listen(3001, () => {
  console.log(`HTTPS server started on port 3001`);
});

